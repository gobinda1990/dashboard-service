package comtax.gov.webapp.service;


import comtax.gov.webapp.model.SignupBean;

public interface SignupService {
	
	/**
     * Checks if a user already exists based on HRMS code or email.
     *     
     */
    boolean userExists(String hrmsCode);
	
	boolean registerUser(SignupBean signupBean);	
	
}
