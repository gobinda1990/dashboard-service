package comtax.gov.webapp.controller;

import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import comtax.gov.webapp.exception.ErrorCode;
import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.ApiResponse;
import comtax.gov.webapp.model.SignupBean;
import comtax.gov.webapp.model.common.DesignDet;
import comtax.gov.webapp.service.CommonService;
import comtax.gov.webapp.service.SignupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for handling user signup and related metadata endpoints.
 */
@RestController
@RequestMapping("/auth")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "https://10.153.43.8:8084")
public class SignupController {

	private final CommonService commonService;
	private final SignupService signupService;

	// =====================================================
	// === User Registration ===============================
	// =====================================================

	/**
	 * Registers a new user based on HRMS code and user details.
	 */
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody SignupBean signupBean,
			HttpServletRequest request) {
		log.info("Received signup request for HRMS code: {}", signupBean.getHrms_code());
		try {
			// Check for duplicate user
			if (signupService.userExists(signupBean.getHrms_code())) {
				log.warn("Signup failed: HRMS {} already exists", signupBean.getHrms_code());
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(ApiResponse.<String>builder().status(HttpStatus.CONFLICT.value())
								.message("User already exists").errorCode(ErrorCode.DUPLICATE_RESOURCE).success(false)
								.path(request.getRequestURI()).build());
			}

			// Attempt registration
			boolean isSaved = signupService.registerUser(signupBean);
			if (!isSaved) {
				log.warn("Signup failed due to persistence error for HRMS {}", signupBean.getHrms_code());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(ApiResponse.<String>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value())
								.message("User registration failed. Please try again later.")
								.errorCode(ErrorCode.SAVE_OPERATION_FAILED).success(false).path(request.getRequestURI())
								.build());
			}

			// Success
			log.info("User registered successfully for HRMS: {}", signupBean.getHrms_code());
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(ApiResponse.<String>builder().status(HttpStatus.CREATED.value())
							.message("User registered successfully").data(signupBean.getHrms_code()).success(true)
							.path(request.getRequestURI()).build());

		} catch (ServiceException se) {
			log.error("Service exception during signup for HRMS {}: {}", signupBean.getHrms_code(), se.getMessage());
			throw se;

		} catch (Exception ex) {
			log.error("Unexpected error during signup for HRMS {}: {}", signupBean.getHrms_code(), ex.getMessage(), ex);
			throw new ServiceException("Unexpected error during signup for HRMS: " + signupBean.getHrms_code());
		}
	}

	// =====================================================
	// === Designation Metadata ============================
	// =====================================================

	/**
	 * Fetches all designations from the system.
	 */
	@GetMapping("/designation_details")
	public ResponseEntity<ApiResponse<List<DesignDet>>> getAllDesignations(HttpServletRequest request) {
		log.info("Fetching all designation details...");

		try {
			List<DesignDet> designs = commonService.fetchAllDesignations();

			if (designs == null || designs.isEmpty()) {
				log.warn("No designation details found in the system");
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(ApiResponse.<List<DesignDet>>builder().status(HttpStatus.NO_CONTENT.value())
								.message("No designation details found").data(Collections.emptyList()).success(true)
								.path(request.getRequestURI()).build());
			}

			log.info("Fetched {} designation records successfully", designs.size());
			return ResponseEntity.ok(ApiResponse.<List<DesignDet>>builder().status(HttpStatus.OK.value())
					.message("Designation details fetched successfully").data(designs).success(true)
					.path(request.getRequestURI()).build());

		} catch (ServiceException se) {
			log.error("Service error while fetching designation details: {}", se.getMessage(), se);
			throw se; // centralized handling via GlobalExceptionHandler

		} catch (Exception ex) {
			log.error("Unexpected error while fetching designation details: {}", ex.getMessage(), ex);
			throw new ServiceException("Unexpected error while fetching designation details", ex);
		}
	}
}
