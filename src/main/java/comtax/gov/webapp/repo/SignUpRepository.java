package comtax.gov.webapp.repo;

import comtax.gov.webapp.model.SignupBean;

public interface SignUpRepository {

	
	String SIGN_UP_SQL="INSERT INTO impact2_user_master (hrms_code, passwd, full_name, email, phone_no,usr_status_cd, usr_level_cd, "
			+ " desig_cd, gpf_no, circle_cd,charge_cd, dt_of_join, pan_no, bo_id, dt_of_birth,hint_qs_cd, hint_ans, "
			+ " office_cd, gender,log_dt) VALUES (:hrms_code,:passwd,:full_name,:email,:phone_no,:usr_status_cd,:usr_level_cd, "
			+ " :desig_cd,:gpf_no,:circle_cd,:charge_cd,:dt_of_join,:pan_no,:bo_id,:dt_of_birth,:hint_qs_cd,:hint_ans, "
			+ " :office_cd,:gender,CURRENT_TIMESTAMP)";
	
	boolean saveSignupData(SignupBean signupBean);
	
    boolean userExists(String hrmsCode);
}

