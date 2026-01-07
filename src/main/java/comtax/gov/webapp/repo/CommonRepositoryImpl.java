package comtax.gov.webapp.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import comtax.gov.webapp.exception.DataSaveException;
import comtax.gov.webapp.exception.DatabaseOperationException;
import comtax.gov.webapp.model.AddModuleRequest;
import comtax.gov.webapp.model.AuthUserDetails;
import comtax.gov.webapp.model.EmployeeCountSummary;
import comtax.gov.webapp.model.RoleDet;
import comtax.gov.webapp.model.common.ChargeDet;
import comtax.gov.webapp.model.common.CircleDet;
import comtax.gov.webapp.model.common.DesignDet;
import comtax.gov.webapp.model.common.OfficeDet;
import comtax.gov.webapp.model.common.ProjectDet;
import comtax.gov.webapp.model.common.UserDet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CommonRepositoryImpl implements CommonRepository {

	private final JdbcTemplate jdbcTemplate;

	private static final String POSTING_TYPE = "M";

	@Override
	public List<ProjectDet> fetchAllProjects(AuthUserDetails authUserDet) {

		String role_id = "";
		String role = "";

		log.info("CommonRepositoryImpl role size =" + authUserDet.getRole().size());
		if (authUserDet.getRole().size() > 0) {
			role = authUserDet.getRole().get(0).replaceFirst("ROLE_", "");
			log.info("CommonRepositoryImpl role =" + role);

			if (role.equals("Super Admin")) {
				role_id = "R1";

			} else if (role.equals("Admin")) {
				role_id = "R2";

			} else if (role.equals("User")) {
				role_id = "R3";

			}

		}
		log.info("CommonRepositoryImpl role_id=" + role_id);

		if (role_id.equals("R1")) {

			// final_query=FETCH_PROJECT_SQL;
			log.info("Common R1 role_id=" + role_id);
			return jdbcTemplate.query(FETCH_PROJECT_SQL, new BeanPropertyRowMapper<>(ProjectDet.class));
		} else {
			log.info("Common Others role_id=" + role_id);
			// final_query=FETCH_PROJECT_SQL_user;
			return jdbcTemplate.query(FETCH_PROJECT_SQL_user, new BeanPropertyRowMapper<>(ProjectDet.class),
					authUserDet.getHrmsCd());
		}

	}

	@Override
	public List<UserDet> fetchAllUserDetails() {
		log.info("Enter into fetchAllUserDetails:--");
		return jdbcTemplate.query(SELECT_USER_ALL, (rs, rowNum) -> {
			UserDet user = new UserDet();
			user.setHrmsCode(rs.getString("hrmsCode"));
			user.setFullName(rs.getString("fullName"));
			user.setEmail(rs.getString("email"));
			user.setPhoneNo(rs.getString("phoneNo"));
			user.setDesigName(rs.getString("desigName"));
			user.setGpfNo(rs.getString("gpfNo"));
			user.setPanNo(rs.getString("panNo"));
			user.setBoId(rs.getString("boId"));
			user.setProfileImageUrl(rs.getString("profileImageUrl"));
			return user;
		}, "A");
	}

	@Override
	public List<CircleDet> fetchAllCircles() {
		return jdbcTemplate.query(FETCH_CIRCLE_SQL, new BeanPropertyRowMapper<>(CircleDet.class));
	}

	@Override
	public List<ChargeDet> fetchAllCharges() {
		return jdbcTemplate.query(FETCH_CHARGE_SQL, new BeanPropertyRowMapper<>(ChargeDet.class));
	}

	@Override
	public List<DesignDet> fetchAllDesignations() {
		return jdbcTemplate.query(FETCH_DESIGNATION_SQL, new BeanPropertyRowMapper<>(DesignDet.class));
	}

	@Override
	public List<OfficeDet> fetchAllOffices() {
		return jdbcTemplate.query(FETCH_OFFICE_SQL, new BeanPropertyRowMapper<>(OfficeDet.class));
	}

	@Override
	public List<RoleDet> fetchAllRoles() {
		return jdbcTemplate.query(FETCH_ROLE_SQL, new BeanPropertyRowMapper<>(RoleDet.class));
	}

	@Override
	public UserDet getProfileDetails(String hrms_code) {

		return jdbcTemplate.queryForObject(SELECT_USER_DET, new RowMapper<UserDet>() {
			@Override
			public UserDet mapRow(ResultSet rs, int rowNum) throws SQLException {
				UserDet user = new UserDet();
				user.setHrmsCode(rs.getString("hrmsCode"));
				user.setFullName(rs.getString("fullName"));
				user.setEmail(rs.getString("email"));
				user.setPhoneNo(rs.getString("phoneNo"));
				user.setDesigName(rs.getString("desigName"));
				user.setGpfNo(rs.getString("gpfNo"));
				user.setPanNo(rs.getString("panNo"));
				user.setBoId(rs.getString("boId"));
//				user.setProfileImageUrl(
//						"http://localhost:8082/api/uploads/profile-pics/" + rs.getString("profileImageUrl"));
				user.setProfileImageUrl(
						"http://10.153.43.8:8082/api/uploads/profile-pics/" + rs.getString("profileImageUrl"));

				return user;
			}
		}, hrms_code);

	}

	@Override
	public void uploadProfileImg(String hrms, String img_url) {

		jdbcTemplate.update(UPSERT_PROFILE_IMG_URL_SQL, hrms, img_url, "L");

	}

	@Override
	public EmployeeCountSummary getCountForReport() {
		EmployeeCountSummary summary = jdbcTemplate.queryForObject(REPORT_COUNT_SQL, (rs, rowNum) -> {
			EmployeeCountSummary s = new EmployeeCountSummary();
			s.setAssignedCount(rs.getInt("assigned_count"));
			s.setCommonPoolCount(rs.getInt("common_pool_count"));
			return s;
		});
		return summary;
	}

	@Override
	public int releaseEmployee(String hrms) {

		int row_updated = 0;

		row_updated += jdbcTemplate.update(RELEASE_EMP_SQL1, hrms);
		row_updated += jdbcTemplate.update(RELEASE_EMP_SQL2, hrms);

		return row_updated;
	}

	@Override
	public UserDet fetchCurrentUserDetails(String hrmsCode) {
		log.info("Fetching user details for HRMS Code: {}", hrmsCode);

		// Step 1: Fetch base user info
		UserDet user;
		try {
			user = jdbcTemplate.queryForObject(SQL_USER, (rs, rowNum) -> {
				var u = new UserDet();
				u.setHrmsCode(rs.getString("hrms_code"));
				u.setFullName(rs.getString("full_name"));
				return u;
			}, hrmsCode);
		} catch (EmptyResultDataAccessException e) {
			log.warn("No user found for HRMS Code: {}", hrmsCode);
			return null;
		}

		// Step 2: Fetch office types
		var officeTypes = jdbcTemplate.query(SQL_OFFICE_TYPES, ps -> {
			ps.setString(1, hrmsCode);
			ps.setString(2, POSTING_TYPE);
		}, (rs, rowNum) -> rs.getString("office_type"));

		user.setOfficeTypes(officeTypes);
		log.info("Office types for user {}: {}", hrmsCode, officeTypes);

		var circleCds = new ArrayList<String>();
		var chargeCds = new ArrayList<String>();
		var officeCds = new ArrayList<String>();

		for (String officeType : officeTypes) {
			switch (officeType) {
			case "CI" -> {
				circleCds.addAll(jdbcTemplate.query(SQL_CIRCLE, ps -> {
					ps.setString(1, hrmsCode);
					ps.setString(2, POSTING_TYPE);
				}, (rs, rowNum) -> rs.getString("office_cd")));

				user.setCircleCds(circleCds);
				log.info("Circle offices for {}: {}", hrmsCode, circleCds);

				if (!circleCds.isEmpty()) {
					var placeholders = String.join(",", Collections.nCopies(circleCds.size(), "?"));
					var chargeQuery = String.format(SQL_CHARGE_BY_CIRCLE_TEMPLATE, placeholders);
					chargeCds.addAll(jdbcTemplate.query(chargeQuery, ps -> {
						for (int i = 0; i < circleCds.size(); i++) {
							ps.setString(i + 1, circleCds.get(i));
						}
					}, (rs, rowNum) -> rs.getString("charge_cd")));
					user.setChargeCds(chargeCds);
					log.info("Charges under circles {}: {}", circleCds, chargeCds);
				}
			}
			case "CH" -> {
				chargeCds.addAll(jdbcTemplate.query(SQL_CHARGE, ps -> {
					ps.setString(1, hrmsCode);
					ps.setString(2, POSTING_TYPE);
				}, (rs, rowNum) -> rs.getString("office_cd")));
				user.setChargeCds(chargeCds);
				log.info("Charge offices for {}: {}", hrmsCode, chargeCds);
			}
			case "OF" -> {
				officeCds.addAll(jdbcTemplate.query(SQL_OFFICE, ps -> {
					ps.setString(1, hrmsCode);
					ps.setString(2, POSTING_TYPE);
				}, (rs, rowNum) -> rs.getString("office_cd")));
				user.setOfficeCds(officeCds);
				log.info("Office codes for {}: {}", hrmsCode, officeCds);
			}
			default -> log.debug("Unhandled office type: {}", officeType);
			}
		}
		log.info("Fetched user details successfully for HRMS Code: {}", hrmsCode);
		return user;
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

}
