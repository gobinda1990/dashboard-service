package comtax.gov.webapp.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.*;
import comtax.gov.webapp.model.common.*;
import comtax.gov.webapp.service.CommonService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for dashboard-related APIs: user info, circle, office, profile
 * management, reports, and more.
 */
@RestController
@RequestMapping("/dashboard")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "https://10.153.43.8:8084")
public class CommonController {

	private final CommonService commonService;

	@Value("${upload.path}")
	private String uploadPath;

	@Value("${file.base-url}")
	private String fileBaseUrl;

	// =====================================================
	// === PROJECT DETAILS ================================
	// =====================================================

	@GetMapping("/project-details")
	public ResponseEntity<ApiResponse<List<ProjectDet>>> getProjectDetails(@AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {
		String userId = jwt.getSubject();
		log.info("Fetching project details for HRMS: {}", userId);
		try {
			AuthUserDetails authUser = buildAuthUserDetails(jwt);
			List<ProjectDet> projects = commonService.fetchAllProjects(authUser);
			if (projects == null || projects.isEmpty()) {
				log.warn("No project details found for HRMS: {}", userId);
				return noContent("No project details found", request);
			}
			return ok(projects, "Project details fetched successfully", request);
		} catch (Exception ex) {
			log.error("Error fetching project details for HRMS {}: {}", userId, ex.getMessage(), ex);
			throw new ServiceException("Failed to fetch project details for HRMS: " + userId, ex);
		}
	}

	// =====================================================
	// === PROFILE MANAGEMENT ==============================
	// =====================================================

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserDet>> getProfile(@AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {
		String hrms = jwt.getSubject();
		log.info("Fetching profile for HRMS: {}", hrms);
		try {
			UserDet user = commonService.getProfileDetails(hrms);			
			List<String> roles = jwt.getClaimAsStringList("roles").stream()
                    .map(r -> r.replaceFirst("ROLE_", ""))
                    .collect(Collectors.toList());
            user.setRole(roles);
			return ok(user, "Profile fetched successfully", request);
		} catch (Exception ex) {
			log.error("Error fetching profile for HRMS {}: {}", hrms, ex.getMessage(), ex);
			throw new ServiceException("Failed to fetch profile for HRMS: " + hrms, ex);
		}
	}

	@PutMapping("/profile")
	public ResponseEntity<ApiResponse<String>> updateProfile(@AuthenticationPrincipal Jwt jwt,
			@RequestBody UserDet updated, HttpServletRequest request) {
		String hrms = jwt.getSubject();
		log.info("Updating profile for HRMS: {}", hrms);
		try {			
			return ok("Profile updated successfully", request);
		} catch (Exception ex) {
			log.error("Error updating profile for HRMS {}: {}", hrms, ex.getMessage(), ex);
			throw new ServiceException("Failed to update profile for HRMS: " + hrms, ex);
		}
	}

	@PostMapping("/profile/upload")
	public ResponseEntity<ApiResponse<String>> uploadProfileImage(@AuthenticationPrincipal Jwt jwt,
			@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
		String hrms = jwt.getSubject();
		log.info("Uploading profile image for HRMS: {}, file: {}", hrms, file.getOriginalFilename());

		try {
			if (file.isEmpty()) {
				throw new ServiceException("Uploaded file is empty.");
			}

			// Ensure directory exists
			File dir = new File(uploadPath);
			if (!dir.exists() && !dir.mkdirs()) {
				throw new IOException("Failed to create upload directory.");
			}

			// Sanitize filename
			String sanitizedFilename = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9_.-]", "_");
			String finalFilename = hrms + "_" + sanitizedFilename;
			File destination = new File(uploadPath + File.separator + finalFilename);

			file.transferTo(destination);
			log.info("Profile image saved at {}", destination.getAbsolutePath());

			// Update database reference
			commonService.uploadProfileImg(hrms, finalFilename);

			String fileUrl = fileBaseUrl + finalFilename;
			log.info("Profile image accessible at {}", fileUrl);

			return ResponseEntity.ok(ApiResponse.<String>builder().status(HttpStatus.OK.value())
					.message("Profile image uploaded successfully").data(fileUrl).success(true)
					.path(request.getRequestURI()).build());
		} catch (Exception ex) {
			log.error("Error uploading profile image for HRMS {}: {}", hrms, ex.getMessage(), ex);
			throw new ServiceException("Failed to upload profile image for HRMS: " + hrms, ex);
		}
	}
	

	// =====================================================
	// === REPORTS & ACTIONS ===============================
	// =====================================================

	@GetMapping("/reports")
	public ResponseEntity<ApiResponse<EmployeeCountSummary>> getReportOfEmployee(@AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {
		String hrms = jwt.getSubject();
		log.info("Generating employee count summary for HRMS: {}", hrms);
		try {
			EmployeeCountSummary summary = commonService.getCountForReport();
			return ok(summary, "Report fetched successfully", request);
		} catch (Exception ex) {
			log.error("Error fetching employee report: {}", ex.getMessage(), ex);
			throw new ServiceException("Failed to generate employee report", ex);
		}
	}

	@GetMapping("/release-emp/{id}")
	public ResponseEntity<ApiResponse<String>> releaseEmployee(@AuthenticationPrincipal Jwt jwt,
			@PathVariable("id") String id, HttpServletRequest request) {
		String approver = jwt.getSubject();
		log.info("Releasing employee {} by approver {}", id, approver);
		try {
			String message = commonService.releaseEmployee(id);
			return ok(message, "Employee released successfully", request);
		} catch (Exception ex) {
			log.error("Error releasing employee {}: {}", id, ex.getMessage(), ex);
			throw new ServiceException("Failed to release employee " + id, ex);
		}
	}
	
	@PostMapping("/add-module")
	public ResponseEntity<ApiResponse<String>> add_module(@Valid @RequestBody AddModuleRequest req,
			@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
		log.info("Received assignment request: {} by user {}", req, jwt.getSubject());
		try {
			commonService.addModule(req);
			return buildSuccessResponse("Project Add Successfully", jwt.getSubject(), request);
		} catch (ServiceException se) {
			log.error("Service error during add Module {}: {}",  se.getMessage(), se);
			throw se;
			
		} catch (Exception ex) {
			log.error("Unexpected error during addmodule {}: {}",  ex.getMessage(), ex);
			throw new ServiceException("Unexpected error during add Module " , ex);
			
		}
	}

	// =====================================================
	// === Utility Methods ================================
	// =====================================================

	private AuthUserDetails buildAuthUserDetails(Jwt jwt) {
		List<String> roles = jwt.getClaimAsStringList("roles");
		boolean isAdmin = roles != null && roles.contains("ROLE_ADMIN");

		return AuthUserDetails.builder().hrmsCd(jwt.getClaimAsString("hrmsCd")).emailId(jwt.getClaimAsString("emailId"))
				.circleCd(jwt.getClaimAsString("circleCd")).chargeCd(jwt.getClaimAsString("chargeCd"))
				.phoneNo(jwt.getClaimAsString("phoneNo")).gpfNo(jwt.getClaimAsString("gpfNo"))
				.panNo(jwt.getClaimAsString("panNo")).boId(jwt.getClaimAsString("boId")).role(roles).admin(isAdmin)
				.build();
	}	

	private <T> ResponseEntity<ApiResponse<T>> ok(T data, String message, HttpServletRequest request) {
		return ResponseEntity.ok(ApiResponse.<T>builder().status(HttpStatus.OK.value()).message(message).data(data)
				.success(true).path(request.getRequestURI()).build());
	}

	private ResponseEntity<ApiResponse<String>> ok(String message, HttpServletRequest request) {
		return ResponseEntity.ok(ApiResponse.<String>builder().status(HttpStatus.OK.value()).message(message)
				.success(true).path(request.getRequestURI()).build());
	}

	private <T> ResponseEntity<ApiResponse<List<T>>> noContent(String message, HttpServletRequest request) {
		return ResponseEntity.status(HttpStatus.NO_CONTENT)
				.body(ApiResponse.<List<T>>builder().status(HttpStatus.NO_CONTENT.value()).message(message)
						.data(Collections.emptyList()).success(true).path(request.getRequestURI()).build());
	}
	
	private ResponseEntity<ApiResponse<String>> buildSuccessResponse(String msg, String hrmsCode,
			HttpServletRequest request) {
		return ResponseEntity.ok(ApiResponse.<String>builder().status(HttpStatus.OK.value()).message(msg).data(hrmsCode)
				.success(true).path(request.getRequestURI()).build());
	}
}
