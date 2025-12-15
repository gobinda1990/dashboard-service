package comtax.gov.webapp.repo;

import comtax.gov.webapp.model.SignupBean;

public interface SignUpRepository {
	
	String SIGN_UP_SQL = "INSERT INTO impact2_user_master (hrms_code, passwd, full_name, email, phone_no,"
			+ " usr_status_cd, usr_level_cd, desig_cd, gpf_no, circle_cd, charge_cd, dt_of_join, pan_no, bo_id,log_dt,dt_of_birth,hint_qs_cd,hint_ans,office_cd,gender) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,CURRENT_TIMESTAMP,?,?,?,?,?)";

    /**
     * new user record in the impact2_user_master table.
     * @return true if insert successful, false otherwise
     */
    boolean saveSignupData(SignupBean signupBean);

    /**
     * Checks if a user already exists based on HRMS code or email.
     *     
     */
    boolean userExists(String hrmsCode);
}

