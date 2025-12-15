package comtax.gov.webapp.repo;

import java.util.List;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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
		
		String role_id="";
		String role="";
		String final_query="";
		
		log.info("CommonRepositoryImpl role size ="+authUserDet.getRole().size());
		if(authUserDet.getRole().size()>0) {
			role=authUserDet.getRole().get(0).replaceFirst("ROLE_", "");
			log.info("CommonRepositoryImpl role ="+role);
			
			if(role.equals("Super Admin")) {
				role_id="R1";
				
			}else if(role.equals("Charge Approver"))  {
				role_id="R2";
				
			}else if(role.equals("Circle Approver"))  {
				role_id="R3";
				
			}else if(role.equals("Office Approver"))  {
				role_id="R4";
				
			}else if(role.equals("User"))  {
				role_id="R5";
				
			}
			
		}
		log.info("CommonRepositoryImpl role_id="+role_id);
		
		if (role_id.equals("R1")) {
			
			//final_query=FETCH_PROJECT_SQL;
			log.info("Common R1 role_id="+role_id);
			return jdbcTemplate.query(FETCH_PROJECT_SQL, new BeanPropertyRowMapper<>(ProjectDet.class));
		}else {
			log.info("Common Others role_id="+role_id);
			//final_query=FETCH_PROJECT_SQL_user;
			return jdbcTemplate.query(FETCH_PROJECT_SQL_user, new BeanPropertyRowMapper<>(ProjectDet.class),authUserDet.getHrmsCd(),role_id);
		}
			
		
	}
	
	@Override
	public List<UserDet> fetchAllUserDetails() {
		log.info("Enter into fetchAllUserDetails:--");
		return jdbcTemplate.query(FETCH_USER_SQL, new BeanPropertyRowMapper<>(UserDet.class),"A");
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
		
		return jdbcTemplate.query(FETCH_PROFILE_SQL, new Object[]{hrms_code}, rs -> {
	        if(rs.next()) {
	        	  UserDet user = new UserDet();
	              user.setHrmsCode(rs.getString("hrmsCode"));
	              user.setFullName(rs.getString("fullName"));
	              user.setEmail(rs.getString("email"));
	              user.setPhoneNo(rs.getString("phoneNo"));
	              user.setDesigCd(rs.getString("desigCd"));
	              user.setGpfNo(rs.getString("gpfNo"));
	              user.setPanNo(rs.getString("panNo"));
	              user.setBoId(rs.getString("boId"));
	              user.setProfileImageUrl("http://localhost:8082/api/uploads/profile-pics/"+rs.getString("profileImageUrl"));

	              return user;
	        }
	        return null;
	    });
		
	}

	@Override
	public void uploadProfileImg(String hrms,String img_url) {
		
		
		jdbcTemplate.update(INSERT_PROFILE_IMG_URL_SQL, hrms,img_url,"L");
		
	}

	@Override
	public EmployeeCountSummary getCountForReport() {
		EmployeeCountSummary summary = jdbcTemplate.queryForObject(
				REPORT_COUNT_SQL,
			    (rs, rowNum) -> {
			        EmployeeCountSummary s = new EmployeeCountSummary();
			        s.setAssignedCount(rs.getInt("assigned_count"));
			        s.setCommonPoolCount(rs.getInt("common_pool_count"));
			        return s;
			    }
			);
		return summary;
	}

	@Override
	public int releaseEmployee(String hrms) {
		
		int row_updated =0;
		
		row_updated += jdbcTemplate.update(RELEASE_EMP_SQL1,hrms);
		row_updated += jdbcTemplate.update(RELEASE_EMP_SQL2,hrms);
		
		
		return row_updated;
	}
}
