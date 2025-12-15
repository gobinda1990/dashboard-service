package comtax.gov.webapp.service;

import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.SignupBean;
import comtax.gov.webapp.repo.SignUpRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SignupServiceImpl implements SignupService {

	private final SignUpRepository signUpRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public boolean userExists(String hrmsCode) {
		log.info("Enter into userExists:");
		return signUpRepository.userExists(hrmsCode);
	}

	@Override
	@Transactional()
	public boolean registerUser(SignupBean signupBean) {
		log.info("Starting user registration for HRMS Code: {}", signupBean.getHrms_code());
		try {
			String encodedPassword = passwordEncoder.encode(signupBean.getPasswd());
			signupBean.setPasswd(encodedPassword);
			boolean isSaved = signUpRepository.saveSignupData(signupBean);
			if (isSaved) {
				log.info("User registration successful for HRMS Code: {}", signupBean.getHrms_code());
				return true;
			} else {
				log.warn(" Failed to register user for HRMS Code: {}", signupBean.getHrms_code());
				return false;
			}
		} catch (DataAccessException re) {
			log.error("Database error during signup for HRMS Code: {}", signupBean.getHrms_code(), re);
			throw new ServiceException("Database error occurred during signup.", re);

		} catch (Exception ex) {
			log.error("Unexpected error during signup for HRMS Code: {}", signupBean.getHrms_code(), ex);
			throw new ServiceException("Unexpected error occurred during signup.", ex);
		}

	}

}
