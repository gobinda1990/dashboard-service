package comtax.gov.webapp.controller;


import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.ApiResponse;
import comtax.gov.webapp.model.SignupBean;
import comtax.gov.webapp.model.common.DesignDet;
import comtax.gov.webapp.service.CommonService;
import comtax.gov.webapp.service.SignupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SignupController {

	private final CommonService commonService;
	private final SignupService signupService;	
    
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody SignupBean signupBean) {
		
//		System.out.println("date_of_join=="+signupBean.getDt_of_join()+"=date_of_birth="+signupBean.getDt_of_birth());

		log.info("Received signup request for HRMS code: {}", signupBean.getHrms_code());

		try {	
			boolean isRegistered = signupService.userExists(signupBean.getHrms_code());
          //  log.info(""+isRegistered);
			if (isRegistered) {
				log.warn("Signup failed for HRMS code: {} (duplicate)", signupBean.getHrms_code());
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new ApiResponse<>(409, "User already exists ", null));
			}
            
			boolean isSave=signupService.registerUser(signupBean);
			if(!isSave) {
				log.warn("Signup failed for HRMS code: {} ( error)", signupBean.getHrms_code());
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new ApiResponse<>(409, "registration failed", null));	
			}
			log.info("User registered successfully for HRMS code: {}", signupBean.getHrms_code());
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new ApiResponse<>(201, "User registered successfully", signupBean.getHrms_code()));

		} catch (ServiceException se) {
			log.error("Service error during signup for HRMS code {}: {}",se);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(500, "Failed to register user: " + se.getMessage(), null));

		} catch (Exception ex) {
			log.error("Unexpected error during signup for HRMS code {}: {}",ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(500, "Unexpected server error: " + ex.getMessage(), null));
		}
	}
	
	@GetMapping("/designation_details")
	public ResponseEntity<ApiResponse<List<DesignDet>>> getAllDesignations() {
		log.info("Received request: /designation-details");

		try {
			List<DesignDet> designs = commonService.fetchAllDesignations();

			if (designs == null || designs.isEmpty()) {
				log.warn("No designation details found");
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(new ApiResponse<>(204, "No designation details found", Collections.emptyList()));
			}	
			System.out.println("designs"+designs);
			return ResponseEntity
					.ok(new ApiResponse<>(HttpStatus.OK.value(), "designation details fetched successfully", designs));

		} catch (ServiceException se) {
			log.error("Service error while fetching designation", se.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500,
					"Failed to fetch designation details: " + se.getMessage(), Collections.emptyList()));

		} catch (Exception ex) {
			log.error("Unexpected error while fetching designation", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					new ApiResponse<>(500, "Unexpected server error: " + ex.getMessage(), Collections.emptyList()));
		}

}
}
