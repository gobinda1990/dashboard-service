package comtax.gov.webapp.repo;

import comtax.gov.webapp.exception.DataSaveException;
import comtax.gov.webapp.exception.DatabaseOperationException;
import comtax.gov.webapp.mapper.*;
import comtax.gov.webapp.model.AddModuleRequest;
import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.ModuleRow;
import comtax.gov.webapp.model.PostingDet;
import comtax.gov.webapp.model.ReleaseModuleRequest;
import comtax.gov.webapp.model.ReleasePostingRequest;
import comtax.gov.webapp.model.UserAssignDet;
import comtax.gov.webapp.model.UserAssignPostingDet;
import comtax.gov.webapp.model.UserAssignProjectDet;
import comtax.gov.webapp.model.UserAssignRequest;
import comtax.gov.webapp.model.UserReleaseRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AssignEmpRepoImpl implements AssignEmpRepo {

	private final JdbcTemplate jdbcTemplate;

	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String STATUS_ACTIVE = "L";

	private static final String STATUS_INACTIVE = "A";

	/**
	 * Save additional postings for an employee (e.g., multiple offices).
	 */
	@Override
	public boolean saveAdditionalPosting(UserAssignRequest userAssignReq, String assignHrmsCd) {
		log.info("Saving additional postings for HRMS: {}", userAssignReq.getHrmsCode());
		try {
			List<PostingDet> postings = new ArrayList<>(userAssignReq.getPostings());
			List<Object[]> batchArgs = postings.stream()
					.map(det -> new Object[] { userAssignReq.getHrmsCode(), det.getPostingType(), det.getOfficeType(),
							det.getOfficeId(), LocalDate.now(), null, STATUS_ACTIVE, assignHrmsCd, null })
					.collect(Collectors.toList());

			int[] cnt = jdbcTemplate.batchUpdate(INSERT_USER_POSTING_DET, batchArgs);

			if (cnt.length > 0) {
				log.debug("Allotment data saved successfully for HRMS: {}", userAssignReq.getHrmsCode());
				return true;
			} else {
				log.warn("No allotment data inserted for HRMS: {}", userAssignReq.getHrmsCode());
				return false;
			}

		} catch (DataAccessException dae) {
			log.error("Database error saving allotment data for HRMS {}", userAssignReq.getHrmsCode(), dae);
			throw new DatabaseOperationException("Error saving allotment data for HRMS " + userAssignReq.getHrmsCode(),
					dae);
		} catch (Exception e) {
			log.error("Unexpected error saving allotment data for HRMS {}", userAssignReq.getHrmsCode(), e);
			throw new DataSaveException(
					"Unexpected error saving allotment data for HRMS " + userAssignReq.getHrmsCode(), e);
		}
	}

	/**
	 * Save project allocations for an employee.
	 */
	@Override
	public boolean saveAllotProject(AssignRequest request) {
		log.info("Saving project allotments for HRMS: {}", request.getHrmsCode());
		try {
			List<Object[]> batchArgs = new ArrayList<>();
			for (String projectId : request.getProjectIds()) {
				batchArgs.add(new Object[] { request.getHrmsCode(), projectId, STATUS_ACTIVE });
			}

			jdbcTemplate.batchUpdate(insertAllotProjectSQL, batchArgs);
			jdbcTemplate.update("UPDATE impact2_user_master SET usr_status_cd=? WHERE hrms_code=?", STATUS_ACTIVE,
					request.getHrmsCode());

			log.debug("Project allotments saved successfully for HRMS: {}", request.getHrmsCode());
			return true;

		} catch (DataAccessException dae) {
			log.error("Database error saving project allotment for HRMS {}", request.getHrmsCode(), dae);
			throw new DatabaseOperationException("Error saving project allotment for HRMS " + request.getHrmsCode(),
					dae);
		} catch (Exception e) {
			log.error("Unexpected error saving project allotment for HRMS {}", request.getHrmsCode(), e);
			throw new DataSaveException("Unexpected error saving project allotment for HRMS " + request.getHrmsCode(),
					e);
		}
	}

	/**
	 * Save user role assignments.
	 */
	@Override
	public boolean saveRoleForAssign(String hrmsCode, String roleId) {
		log.info("Saving role assignment for HRMS: {}", hrmsCode);
		try {
			int rows = jdbcTemplate.update(insertRoleMasterSQL, hrmsCode, roleId);

			if (rows > 0) {
				log.debug("Role assignment saved successfully for HRMS: {}", hrmsCode);
				return true;
			} else {
				log.warn("No role assignment inserted for HRMS: {}", hrmsCode);
				return false;
			}

		} catch (DataAccessException dae) {
			log.error("Database error while saving role for HRMS {}", hrmsCode, dae);
			throw new DatabaseOperationException("Error saving role for HRMS " + hrmsCode, dae);
		} catch (Exception e) {
			log.error("Unexpected error while saving role for HRMS {}", hrmsCode, e);
			throw new DataSaveException("Unexpected error saving role for HRMS " + hrmsCode, e);
		}
	}

	/**
	 * Save operation log entry for audit.
	 */
	@Override
	public boolean saveAllotLog(AssignRequest request, String opsTabName, String opsType, String assignHrmsCd) {
		log.info("Saving audit log for HRMS: {}", request.getHrmsCode());
		try {
			int rows = jdbcTemplate.update(insertAllotOPSLogSQL, assignHrmsCd, opsTabName, opsType, request.getUserIp(),
					request.getHrmsCode());

			if (rows > 0) {
				log.debug("Audit log saved for HRMS: {}", request.getHrmsCode());
				return true;
			} else {
				log.warn("No audit log entry created for HRMS: {}", request.getHrmsCode());
				return false;
			}

		} catch (DataAccessException dae) {
			log.error("Database error saving audit log for HRMS {}", request.getHrmsCode(), dae);
			throw new DatabaseOperationException("Error saving audit log for HRMS " + request.getHrmsCode(), dae);
		} catch (Exception e) {
			log.error("Unexpected error saving audit log for HRMS {}", request.getHrmsCode(), e);
			throw new DataSaveException("Unexpected error saving audit log for HRMS " + request.getHrmsCode(), e);
		}
	}

	/**
	 * Save main allotment/posting data.
	 */
	@Override
	public boolean saveAllotData(AssignRequest request, String assignHrmsCd) {
		log.info("Saving main allotment data for HRMS: {}", request.getHrmsCode());
		try {
			int rows = jdbcTemplate.update(insertAllotSQL, request.getHrmsCode(), "M", "", request.getOfficeId(),
					assignHrmsCd, request.getRoleId().get(0), request.getChargeCd(), request.getCircleCd());

			if (rows > 0) {
				log.debug("Allotment data saved successfully for HRMS: {}", request.getHrmsCode());
				return true;
			} else {
				log.warn("No allotment data inserted for HRMS: {}", request.getHrmsCode());
				return false;
			}

		} catch (DataAccessException dae) {
			log.error("Database error saving allotment data for HRMS {}", request.getHrmsCode(), dae);
			throw new DatabaseOperationException("Error saving allotment data for HRMS " + request.getHrmsCode(), dae);
		} catch (Exception e) {
			log.error("Unexpected error saving allotment data for HRMS {}", request.getHrmsCode(), e);
			throw new DataSaveException("Unexpected error saving allotment data for HRMS " + request.getHrmsCode(), e);
		}
	}

	/**
	 * Fetch all assigned employees.
	 */
	@Override
	public List<AssignRequest> fetchAssignData(String assignHrmsCd) {
		log.info("Fetching assigned employee data for HRMS (approver): {}", assignHrmsCd);
		try {
			return jdbcTemplate.query(fetchAssigned, new UserPostingRowMapper(), assignHrmsCd);

		} catch (DataAccessException dae) {
			log.error("Database error fetching assigned employees for approver HRMS {}", assignHrmsCd, dae);
			throw new DatabaseOperationException("Error fetching assigned employee data for approver " + assignHrmsCd,
					dae);
		} catch (Exception e) {
			log.error("Unexpected error fetching assigned employees for approver HRMS {}", assignHrmsCd, e);
			throw new DataSaveException("Unexpected error fetching assigned employees for approver " + assignHrmsCd, e);
		}
	}

	@Override
	public List<UserAssignPostingDet> fetchUserPostingDet(String hrmsCd) {
		return jdbcTemplate.query(SELECT_USER_POSTING_DET, new UserAssignPostingDetRowMapper(), hrmsCd, STATUS_ACTIVE);
	}

	@Override
	public List<UserAssignProjectDet> fetchUserProjectDet(String hrmsCd) {
		return jdbcTemplate.query(SELECT_USER_PROJECT_DET, new UserAssignProjectDetRowMapper(), hrmsCd, STATUS_ACTIVE);
	}

	@Override
	public boolean saveUserPostingData(UserAssignRequest userAssignReq, String assignHrmsCd) throws Exception {
		log.info("Saving user posting allotment data for HRMS: {}", userAssignReq.getHrmsCode());
		try {
			List<Object[]> batchArgs = new ArrayList<>();
			for (String postingId : userAssignReq.getPostingIds()) {
				batchArgs.add(new Object[] { userAssignReq.getHrmsCode(), "M", userAssignReq.getPostingType(),
						postingId, LocalDate.now(), null, STATUS_ACTIVE, assignHrmsCd, null });
			}

			int[] cnt = jdbcTemplate.batchUpdate(INSERT_USER_POSTING_DET, batchArgs);

			if (cnt.length > 0) {
				log.debug("Allotment data saved successfully for HRMS: {}", userAssignReq.getHrmsCode());
				return true;
			} else {
				log.warn("No allotment data inserted for HRMS: {}", userAssignReq.getHrmsCode());
				return false;
			}

		} catch (DataAccessException dae) {
			log.error("Database error saving allotment data for HRMS {}", userAssignReq.getHrmsCode(), dae);
			throw new DatabaseOperationException("Error saving allotment data for HRMS " + userAssignReq.getHrmsCode(),
					dae);
		} catch (Exception e) {
			log.error("Unexpected error saving allotment data for HRMS {}", userAssignReq.getHrmsCode(), e);
			throw new DataSaveException(
					"Unexpected error saving allotment data for HRMS " + userAssignReq.getHrmsCode(), e);
		}
	}

	@Override
	public boolean saveUserModulesData(UserAssignRequest userAssignReq, String assignHrmsCd) throws Exception {
		log.info("Saving project allotments for HRMS: {}", userAssignReq.getHrmsCode());
		try {
			List<Object[]> batchArgs = new ArrayList<>();

			for (ModuleRow module : userAssignReq.getModules()) {
				batchArgs.add(new Object[] { userAssignReq.getHrmsCode(), module.getProjectId(), module.getRoleId(),
						STATUS_ACTIVE, LocalDate.now(), null });
			}
			// Batch upsert user project details
			int[] cnt = jdbcTemplate.batchUpdate(INSERT_USER_PROJECT_DET, batchArgs);
			log.info("count:--" + cnt.length);
			// Update master user status
			jdbcTemplate.update("UPDATE impact2_user_master SET usr_status_cd = ? WHERE hrms_code = ?", STATUS_ACTIVE,
					userAssignReq.getHrmsCode());

			log.debug("Project allotments saved successfully for HRMS: {}", userAssignReq.getHrmsCode());
			return true;

		} catch (DataAccessException dae) {
			log.error("Database error saving project allotment for HRMS {}", userAssignReq.getHrmsCode(), dae);
			throw new DatabaseOperationException(
					"Error saving project allotment for HRMS " + userAssignReq.getHrmsCode(), dae);
		} catch (Exception e) {
			log.error("Unexpected error saving project allotment for HRMS {}", userAssignReq.getHrmsCode(), e);
			throw new DataSaveException(
					"Unexpected error saving project allotment for HRMS " + userAssignReq.getHrmsCode(), e);
		}
	}

	@Override
	public List<UserAssignDet> getAllUsersWithPostingsAndProjects(String hrmsCd, String role) {
		log.info("Enter into getAllUsersWithPostingsAndProjects:---" + role);
		List<Map<String, Object>> userRows = null;
		Map<String, UserAssignDet> userMap = null;
		if ("Super Admin".equals(role)) {
			userRows = jdbcTemplate.queryForList(SQL_FETCH_USERS_FROM_POSTINGS);
			userMap = new LinkedHashMap<>();
			log.info("", userMap);
			for (Map<String, Object> row : userRows) {
				String hrmsCode = (String) row.get("hrms_code");
				UserAssignDet user = new UserAssignDet();
				user.setHrmsCode(hrmsCode);
				user.setFullName((String) row.get("full_name"));
				user.setEmail((String) row.get("email"));
				user.setDesigName((String) row.get("desig_name"));
				user.setBoId((String) row.get("bo_id"));
				user.setProfileImageUrl((String) row.get("profile_image_url"));
				user.setPostings(new ArrayList<>());
				user.setProjects(new ArrayList<>());
				userMap.put(hrmsCode, user);
			}
		} else {
			String query = "SELECT office_cd FROM impact2_user_posting_det WHERE hrms_code = ? AND status = ?";
			List<String> officeIds = jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("office_cd"), hrmsCd, "L");
			Map<String, Object> params = new HashMap<>();
			params.put("officeIds", officeIds);
			userRows = namedParameterJdbcTemplate.queryForList(SQL_FETCH_USERS_BASE_POSTINGS, params);
			userMap = new LinkedHashMap<>();
			for (Map<String, Object> row : userRows) {
				String hrmsCode = (String) row.get("hrms_code");
				UserAssignDet user = new UserAssignDet();
				user.setHrmsCode(hrmsCode);
				user.setFullName((String) row.get("full_name"));
				user.setEmail((String) row.get("email"));
				user.setDesigName((String) row.get("desig_name"));
				user.setBoId((String) row.get("bo_id"));
				user.setProfileImageUrl((String) row.get("profile_image_url"));
				user.setPostings(new ArrayList<>());
				user.setProjects(new ArrayList<>());
				userMap.put(hrmsCode, user);
			}

		}

		// Fetch postings for these users ---
		List<Map<String, Object>> postingRows = jdbcTemplate.queryForList(SQL_FETCH_POSTINGS, "L");

		// log.info("size" + postingRows.size());
		for (Map<String, Object> row : postingRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = userMap.get(hrmsCode);
			if (user == null)
				continue; // skip postings not linked to a fetched user

			UserAssignPostingDet posting = new UserAssignPostingDet();
			posting.setPostingType((String) row.get("posting_type"));
			posting.setOfficeType((String) row.get("office_type"));
			posting.setOfficeId((String) row.get("office_id")); // corrected alias
			posting.setOfficeName((String) row.get("office_name"));
			posting.setActiveDt((String) row.get("active_dt"));
			posting.setApproverName((String) row.get("approver_name"));
			posting.setStatus((String) row.get("status"));

			user.getPostings().add(posting);
		}

		// Fetch projects for these users ---
		List<Map<String, Object>> projectRows = jdbcTemplate.queryForList(SQL_FETCH_PROJECTS, "L");
		// log.info("size" + projectRows.size());
		for (Map<String, Object> row : projectRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = userMap.get(hrmsCode);
			if (user == null)
				continue;

			UserAssignProjectDet project = new UserAssignProjectDet();
			project.setProjectId(row.get("project_id") != null ? row.get("project_id").toString() : null);
			project.setProjectName((String) row.get("project_name"));
			project.setRoleId((String) row.get("role_id"));
			project.setRoleName((String) row.get("role_name"));
			project.setStatus((String) row.get("status"));
			project.setActiveDt((String) row.get("active_dt"));
			project.setInactiveDt((String) row.get("inactive_dt"));

			user.getProjects().add(project);
		}

		return new ArrayList<>(userMap.values());
	}

	@Override
	public List<UserAssignDet> getAllUsersWithPostings(String hrmsCd, String role) {
		log.info("Enter into getAllUsersWithPostings:---" + role);
		List<Map<String, Object>> userRows = null;
		Map<String, UserAssignDet> userMap = null;
		userRows = jdbcTemplate.queryForList(SQL_FETCH_USERS_FROM_POSTINGS);
		userMap = new LinkedHashMap<>();
		// log.info("", userMap);
		for (Map<String, Object> row : userRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = new UserAssignDet();
			user.setHrmsCode(hrmsCode);
			user.setFullName((String) row.get("full_name"));
			user.setEmail((String) row.get("email"));
			user.setDesigName((String) row.get("desig_name"));
			user.setBoId((String) row.get("bo_id"));
			user.setProfileImageUrl((String) row.get("profile_image_url"));
			user.setPostings(new ArrayList<>());
			user.setProjects(new ArrayList<>());
			userMap.put(hrmsCode, user);
		}

		// Fetch postings for these users ---
		List<Map<String, Object>> postingRows = jdbcTemplate.queryForList(SQL_FETCH_POSTINGS, "L");

		// log.info("size" + postingRows.size());
		for (Map<String, Object> row : postingRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = userMap.get(hrmsCode);
			if (user == null)
				continue; // skip postings not linked to a fetched user

			UserAssignPostingDet posting = new UserAssignPostingDet();
			posting.setPostingType((String) row.get("posting_type"));
			posting.setOfficeType((String) row.get("office_type"));
			posting.setOfficeId((String) row.get("office_id")); // corrected alias
			posting.setOfficeName((String) row.get("office_name"));
			posting.setActiveDt((String) row.get("active_dt"));
			posting.setApproverName((String) row.get("approver_name"));
			posting.setStatus((String) row.get("status"));

			user.getPostings().add(posting);
		}

		// Fetch projects for these users ---
		List<Map<String, Object>> projectRows = jdbcTemplate.queryForList(SQL_FETCH_PROJECTS, "L");
		// log.info("size" + projectRows.size());
		for (Map<String, Object> row : projectRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = userMap.get(hrmsCode);
			if (user == null)
				continue;

			UserAssignProjectDet project = new UserAssignProjectDet();
			project.setProjectId(row.get("project_id") != null ? row.get("project_id").toString() : null);
			project.setProjectName((String) row.get("project_name"));
			project.setRoleId((String) row.get("role_id"));
			project.setRoleName((String) row.get("role_name"));
			project.setStatus((String) row.get("status"));
			project.setActiveDt((String) row.get("active_dt"));
			project.setInactiveDt((String) row.get("inactive_dt"));

			user.getProjects().add(project);
		}

		return new ArrayList<>(userMap.values());
	}

	@Override
	public boolean releaseUserPostingData(UserReleaseRequest userRelReq, String releaseHrmsCd) throws Exception {
		log.info("Enter into releaseUserPostingData method():---");

		List<ReleasePostingRequest> postings = userRelReq.getReleasePostings();
		if (postings == null || postings.isEmpty()) {
			log.info("No posting data to release for HRMS: {}", userRelReq.getHrmsCode());
			return false;
		}

		int[] results = jdbcTemplate.batchUpdate(UPDATE_USER_POSTING_DET, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ReleasePostingRequest posting = postings.get(i);
				ps.setString(1, STATUS_INACTIVE);
				ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
				ps.setString(3, userRelReq.getHrmsCode());
				ps.setString(4, posting.getPostingType());
				ps.setString(5, posting.getOfficeType());
				ps.setString(6, posting.getOfficeId());
			}

			@Override
			public int getBatchSize() {
				return postings.size();
			}
		});

		boolean success = Arrays.stream(results).allMatch(r -> r >= 0);
		log.info("Posting release completed for HRMS: {} | Rows affected: {}", userRelReq.getHrmsCode(),
				Arrays.stream(results).sum());
		return success;
	}

	@Override
	public boolean releaseUserModulesData(UserReleaseRequest userRelReq, String releaseHrmsCd) throws Exception {
		log.info("Enter into releaseUserModulesData method():---");

		List<ReleaseModuleRequest> modules = userRelReq.getReleaseProjects();
		if (modules == null || modules.isEmpty()) {
			log.info("No project/module data to release for HRMS: {}", userRelReq.getHrmsCode());
			return false;
		}

		int[] results = jdbcTemplate.batchUpdate(UPDATE_USER_PROJECT_DET, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ReleaseModuleRequest module = modules.get(i);
				ps.setString(1, STATUS_INACTIVE);
				ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
				ps.setString(3, userRelReq.getHrmsCode());
				ps.setString(4, module.getProjectId());
				ps.setString(5, module.getRoleId());
			}

			@Override
			public int getBatchSize() {
				return modules.size();
			}
		});

		boolean success = Arrays.stream(results).allMatch(r -> r >= 0);
		log.info("Module release completed for HRMS: {} | Rows affected: {}", userRelReq.getHrmsCode(),
				Arrays.stream(results).sum());
		return success;
	}

	@Override
	public boolean updateUserStatus(String status, String hrmsCd) throws Exception {
		log.info("Enter into updateUserStatus method():---");

		int rowsAffected = jdbcTemplate.update(UPDATE_USER_STATUS, status, hrmsCd);

		if (rowsAffected > 0) {
			log.info("User status updated successfully for HRMS Code: {} | New Status: {}", hrmsCd, status);
			return true;
		} else {
			log.warn("No user record found for HRMS Code: {}", hrmsCd);
			return false;
		}
	}

	@Override
	public int fetchUserPostingCount(String hrmsCd) throws Exception {
		log.info("Enter into fetchUserPostingCount method():---");

		String sql = "SELECT COUNT(*) FROM impact2_user_posting_det WHERE hrms_code = ? AND status = ?";

		// Use varargs instead of Object[] to avoid deprecated method
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, hrmsCd, "L");

		return (count != null) ? count : 0;
	}
	
	@Override
	public boolean addModule(AddModuleRequest bn) {
		log.info("Saving main allotment data for HRMS: {}");
		try {
			int rows = jdbcTemplate.update(INSERT_PROJECT, bn.getModuleName(),bn.getModuleUrl());

			if (rows > 0) {
				log.debug("Allotment data saved successfully for HRMS: {}");
				return true;
			} else {
				log.warn("No allotment data inserted for HRMS: {}");
				return false;
			}

		} catch (DataAccessException dae) {
			log.error("Database error saving allotment data for HRMS {}", dae);
			throw new DatabaseOperationException("Error saving allotment data for HRMS " , dae);
		} catch (Exception e) {
			log.error("Unexpected error saving allotment data for HRMS {}", e);
			throw new DataSaveException("Unexpected error saving allotment data for HRMS " , e);
		}
		//return false;
	}

	@Override
	public List<UserAssignDet> getAllUsersWithPostingsAndProjects(String hrmsCd, String role,String postingType) {
		log.info("Enter into getAllUsersWithPostingsAndProjects:---" + role);
		List<Map<String, Object>> userRows = null;
		Map<String, UserAssignDet> userMap = null;
		if ("Super Admin".equals(role)) {
			userRows = jdbcTemplate.queryForList(SQL_FETCH_USERS_FROM_POSTINGS);
			userMap = new LinkedHashMap<>();
			log.info("", userMap);
			for (Map<String, Object> row : userRows) {
				String hrmsCode = (String) row.get("hrms_code");
				UserAssignDet user = new UserAssignDet();
				user.setHrmsCode(hrmsCode);
				user.setFullName((String) row.get("full_name"));
				user.setEmail((String) row.get("email"));
				user.setDesigName((String) row.get("desig_name"));
				user.setBoId((String) row.get("bo_id"));
				user.setProfileImageUrl((String) row.get("profile_image_url"));
				user.setPostings(new ArrayList<>());
				user.setProjects(new ArrayList<>());
				userMap.put(hrmsCode, user);
			}
		} else {
			String query = "SELECT office_cd FROM impact2_user_posting_det WHERE hrms_code = ? AND status = ?";
			List<String> officeIds = jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("office_cd"), hrmsCd, "L");
			Map<String, Object> params = new HashMap<>();
			params.put("officeIds", officeIds);
			userRows = namedParameterJdbcTemplate.queryForList(SQL_FETCH_USERS_BASE_POSTINGS, params);
			userMap = new LinkedHashMap<>();
			for (Map<String, Object> row : userRows) {
				String hrmsCode = (String) row.get("hrms_code");
				UserAssignDet user = new UserAssignDet();
				user.setHrmsCode(hrmsCode);
				user.setFullName((String) row.get("full_name"));
				user.setEmail((String) row.get("email"));
				user.setDesigName((String) row.get("desig_name"));
				user.setBoId((String) row.get("bo_id"));
				user.setProfileImageUrl((String) row.get("profile_image_url"));
				user.setPostings(new ArrayList<>());
				user.setProjects(new ArrayList<>());
				userMap.put(hrmsCode, user);
			}

		}

		// --- 2️⃣ Fetch postings for these users ---
		List<Map<String, Object>> postingRows = jdbcTemplate.queryForList(SQL_FETCH_POSTINGS, "L",postingType);

		log.info("size" + postingRows.size());
		for (Map<String, Object> row : postingRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = userMap.get(hrmsCode);
			if (user == null)
				continue; // skip postings not linked to a fetched user

			UserAssignPostingDet posting = new UserAssignPostingDet();
			posting.setPostingType((String) row.get("posting_type"));
			posting.setOfficeType((String) row.get("office_type"));
			posting.setOfficeId((String) row.get("office_id")); // corrected alias
			posting.setOfficeName((String) row.get("office_name"));
			posting.setActiveDt((String) row.get("active_dt"));
			posting.setApproverName((String) row.get("approver_name"));
			posting.setStatus((String) row.get("status"));

			user.getPostings().add(posting);
		}

		// --- 3️⃣ Fetch projects for these users ---
		List<Map<String, Object>> projectRows = jdbcTemplate.queryForList(SQL_FETCH_PROJECTS, "L");
		log.info("size" + projectRows.size());
		for (Map<String, Object> row : projectRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = userMap.get(hrmsCode);
			if (user == null)
				continue;

			UserAssignProjectDet project = new UserAssignProjectDet();
			project.setProjectId(row.get("project_id") != null ? row.get("project_id").toString() : null);
			project.setProjectName((String) row.get("project_name"));
			project.setRoleId((String) row.get("role_id"));
			project.setRoleName((String) row.get("role_name"));
			project.setStatus((String) row.get("status"));
			project.setActiveDt((String) row.get("active_dt"));
			project.setInactiveDt((String) row.get("inactive_dt"));

			user.getProjects().add(project);
		}

		return new ArrayList<>(userMap.values());
	}

	@Override
	public List<UserAssignDet> getAssignedUserAM(String hrmsCd, String role) {
		log.info("Enter into getAllUsersWithPostingsAndProjects:---" + role);
		List<Map<String, Object>> userRows = null;
		Map<String, UserAssignDet> userMap = null;
		if ("Super Admin".equals(role)) {
			userRows = jdbcTemplate.queryForList(SQL_FETCH_USERS_FROM_POSTINGS);
			userMap = new LinkedHashMap<>();
			log.info("", userMap);
			for (Map<String, Object> row : userRows) {
				String hrmsCode = (String) row.get("hrms_code");
				UserAssignDet user = new UserAssignDet();
				user.setHrmsCode(hrmsCode);
				user.setFullName((String) row.get("full_name"));
				user.setEmail((String) row.get("email"));
				user.setDesigName((String) row.get("desig_name"));
				user.setBoId((String) row.get("bo_id"));
				user.setProfileImageUrl((String) row.get("profile_image_url"));
				user.setPostings(new ArrayList<>());
				user.setProjects(new ArrayList<>());
				userMap.put(hrmsCode, user);
			}
		} else {
			String query = "SELECT office_cd FROM impact2_user_posting_det WHERE hrms_code = ? AND status = ?";
			List<String> officeIds = jdbcTemplate.query(query, (rs, rowNum) -> rs.getString("office_cd"), hrmsCd, "L");
			Map<String, Object> params = new HashMap<>();
			params.put("officeIds", officeIds);
			userRows = namedParameterJdbcTemplate.queryForList(SQL_FETCH_USERS_BASE_POSTINGS, params);
			userMap = new LinkedHashMap<>();
			for (Map<String, Object> row : userRows) {
				String hrmsCode = (String) row.get("hrms_code");
				UserAssignDet user = new UserAssignDet();
				user.setHrmsCode(hrmsCode);
				user.setFullName((String) row.get("full_name"));
				user.setEmail((String) row.get("email"));
				user.setDesigName((String) row.get("desig_name"));
				user.setBoId((String) row.get("bo_id"));
				user.setProfileImageUrl((String) row.get("profile_image_url"));
				user.setPostings(new ArrayList<>());
				user.setProjects(new ArrayList<>());
				userMap.put(hrmsCode, user);
			}

		}

		// --- 2️⃣ Fetch postings for these users ---
		List<Map<String, Object>> postingRows = jdbcTemplate.queryForList(SQL_FETCH_POSTINGS, "L");

		log.info("size" + postingRows.size());
		for (Map<String, Object> row : postingRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = userMap.get(hrmsCode);
			if (user == null)
				continue; // skip postings not linked to a fetched user

			UserAssignPostingDet posting = new UserAssignPostingDet();
			posting.setPostingType((String) row.get("posting_type"));
			posting.setOfficeType((String) row.get("office_type"));
			posting.setOfficeId((String) row.get("office_id")); // corrected alias
			posting.setOfficeName((String) row.get("office_name"));
			posting.setActiveDt((String) row.get("active_dt"));
			posting.setApproverName((String) row.get("approver_name"));
			posting.setStatus((String) row.get("status"));

			user.getPostings().add(posting);
		}

		// --- 3️⃣ Fetch projects for these users ---
		List<Map<String, Object>> projectRows = jdbcTemplate.queryForList(SQL_FETCH_PROJECTS, "L");
		log.info("size" + projectRows.size());
		for (Map<String, Object> row : projectRows) {
			String hrmsCode = (String) row.get("hrms_code");
			UserAssignDet user = userMap.get(hrmsCode);
			if (user == null)
				continue;

			UserAssignProjectDet project = new UserAssignProjectDet();
			project.setProjectId(row.get("project_id") != null ? row.get("project_id").toString() : null);
			project.setProjectName((String) row.get("project_name"));
			project.setRoleId((String) row.get("role_id"));
			project.setRoleName((String) row.get("role_name"));
			project.setStatus((String) row.get("status"));
			project.setActiveDt((String) row.get("active_dt"));
			project.setInactiveDt((String) row.get("inactive_dt"));

			user.getProjects().add(project);
		}

		return new ArrayList<>(userMap.values());
	}

}
