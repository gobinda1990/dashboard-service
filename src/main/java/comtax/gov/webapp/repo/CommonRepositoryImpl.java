package comtax.gov.webapp.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
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
				user.setProfileImageUrl(
						"http://localhost:8082/api/uploads/profile-pics/" + rs.getString("profileImageUrl"));

				return user;
			}
		}, hrms_code);

	}

	@Override
	public void uploadProfileImg(String hrms, String img_url) {

		jdbcTemplate.update(INSERT_PROFILE_IMG_URL_SQL, hrms, img_url, "L");

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
		String sqlUser = "SELECT hrms_code, full_name FROM impact2_user_master WHERE hrms_code = ?";

		UserDet user = jdbcTemplate.queryForObject(sqlUser, (rs, rowNum) -> {
			UserDet u = new UserDet();
			u.setHrmsCode(rs.getString("hrms_code"));
			u.setFullName(rs.getString("full_name"));
			return u;
		}, hrmsCode);

		if (user == null) {
			log.warn("No user found for HRMS Code: {}", hrmsCode);
			return null;
		}
		// Fetch distinct office types
		String sqlOfficeTypes = "SELECT DISTINCT office_type FROM impact2_user_posting_det WHERE hrms_code = ? AND posting_type = 'M'";

		List<String> officeTypes = jdbcTemplate.query(sqlOfficeTypes, ps -> ps.setString(1, hrmsCode),
				(rs, rowNum) -> rs.getString("office_type"));
		user.setOfficeTypes(officeTypes);

		log.info("Office types for user {}: {}", hrmsCode, officeTypes);
		List<String> circleCds = new ArrayList<>();
		List<String> chargeCds = new ArrayList<>();
		List<String> officeCds = new ArrayList<>();

		for (String officeType : officeTypes) {
			switch (officeType) {
			case "CI" -> {
				String circleQuery = "SELECT DISTINCT office_cd FROM impact2_user_posting_det WHERE hrms_code = ? "
						+ " AND office_type = 'CI' AND posting_type = 'M'";
				circleCds = jdbcTemplate.query(circleQuery, ps -> ps.setString(1, hrmsCode),
						(rs, rowNum) -> rs.getString("office_cd"));
				user.setCircleCds(circleCds);
				log.info("Circle offices for {}: {}", hrmsCode, circleCds);

				if (!circleCds.isEmpty()) {
					String chargeQuery = String.format(
							"SELECT DISTINCT charge_cd FROM charge_cd WHERE circle_cd IN (%s)",
							circleCds.stream().map(cd -> "'" + cd + "'").collect(Collectors.joining(",")));
					chargeCds = jdbcTemplate.query(chargeQuery, (rs, rowNum) -> rs.getString("charge_cd"));
					user.setChargeCds(chargeCds);
					log.info("Charges under circles: {}", chargeCds);
				}
			}
			case "CH" -> {
				String chargeQuery = " SELECT DISTINCT office_cd FROM impact2_user_posting_det WHERE hrms_code = ?"
						+ " AND office_type = 'CH' AND posting_type = 'M'";
				chargeCds = jdbcTemplate.query(chargeQuery, ps -> ps.setString(1, hrmsCode),
						(rs, rowNum) -> rs.getString("office_cd"));
				user.setChargeCds(chargeCds);
				log.info("Charge offices for {}: {}", hrmsCode, chargeCds);
			}

			case "OF" -> {
				String officeQuery = "SELECT DISTINCT office_cd FROM impact2_user_posting_det WHERE hrms_code = ?"
						+ " AND office_type = 'OF' AND posting_type = 'M'";
				officeCds = jdbcTemplate.query(officeQuery, ps -> ps.setString(1, hrmsCode),
						(rs, rowNum) -> rs.getString("office_cd"));
				user.setOfficeCds(officeCds);
				log.info("Office codes for {}: {}", hrmsCode, officeCds);
			}
			}
		}
		log.info("Fetched user details successfully for HRMS Code: {}", hrmsCode);
		return user;
	}

}
