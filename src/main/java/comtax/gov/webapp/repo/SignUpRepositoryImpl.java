package comtax.gov.webapp.repo;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Repository;

import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.SignupBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class SignUpRepositoryImpl implements SignUpRepository {
	
	private final JdbcTemplate jdbcTemplate;
	
	@Override
	public boolean saveSignupData(SignupBean signupBean) {
		try {
			log.info("Enter into SaveSignupData:---");
			java.sql.Date joinDate = null;
			java.sql.Date dtBrith = null;
			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date_join = sdf.parse(signupBean.getDt_of_join().toString());
			Date date_birth = sdf.parse(signupBean.getDt_of_birth().toString());
			
//	        if (signupBean.getDt_of_join() != null) {
//	            // ✅ Convert LocalDate or String to SQL Date safely
//	            if (signupBean.getDt_of_join() instanceof LocalDate) {
//	                joinDate = java.sql.Date.valueOf((LocalDate) signupBean.getDt_of_join());
//	            } else {
//	                // If it's a String, parse first
//	                joinDate = java.sql.Date.valueOf(LocalDate.parse(signupBean.getDt_of_join().toString()));
//	            }
//	        }
//	        
			int rowsUpdated = jdbcTemplate.update(SIGN_UP_SQL, signupBean.getHrms_code(),
					signupBean.getPasswd(), signupBean.getFull_name(), signupBean.getEmail(),
					signupBean.getPhone_no(), "A", signupBean.getUsr_level_cd(), signupBean.getDesig_cd(),
					signupBean.getGpf_no(), signupBean.getCircle_cd(), signupBean.getCharge_cd(),
					date_join, signupBean.getPan_no(), signupBean.getBo_id(),date_birth,signupBean.getHint_qs_cd(),signupBean.getHint_ans(),signupBean.getOffice_cd(),signupBean.getGender());

			if (rowsUpdated > 0) {
			//	log.info("Signup successful for HRMS Code: {}", signupBean.getHrms_code());
				return true;
			} else {
				log.warn("Signup failed: No rows updated for HRMS Code: {}", signupBean.getHrms_code());
				return false;
			}

		} catch (DataAccessException dae) {
			log.error("Database error while saving signup for HRMS Code: {}", signupBean.getHrms_code(), dae);
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unexpected error while saving signup for HRMS Code: {}", signupBean.getHrms_code(), e);
			return false;
		}
	}

	@Override
	public boolean userExists(String hrmsCode) {
	    final String SQL = "SELECT COUNT(*) FROM impact2_user_master WHERE hrms_code = ?";
	    try {
	        Integer count = jdbcTemplate.queryForObject(SQL, Integer.class, hrmsCode);
	        boolean exists = count != null && count > 0;

	        log.debug("User existence check for HRMS Code [{}]: {}", hrmsCode, exists);
	        return exists;

	    } catch (DataAccessException dae) {
	        log.error("Database error while checking user existence for HRMS Code [{}]", hrmsCode);
	        throw new ServiceException("Failed to verify user existence", dae);

	    } catch (Exception ex) {
	        log.error("Unexpected error while verifying user existence for HRMS Code [{}]", hrmsCode, ex);
	        throw new ServiceException("Unexpected error while verifying user existence", ex);
	    }
	}

	
}
