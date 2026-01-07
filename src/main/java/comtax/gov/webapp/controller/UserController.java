package comtax.gov.webapp.controller;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.*;
import comtax.gov.webapp.model.common.*;
import comtax.gov.webapp.service.AssignService;
import comtax.gov.webapp.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller handling user, role, circle, charge, office, and assignment
 * operations. Optimized for production performance and maintainability.
 */
@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Consider restricting or externalizing allowed origins for production
@Validated
public class UserController {

	private final AssignService assignService;
	private final CommonService commonService;

	@GetMapping("/current-user")
	public ResponseEntity<ApiResponse<UserDet>> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
		String hrmsCode = jwt.getSubject();
		log.info("Fetching current user details for HRMS: {}", hrmsCode);

		try {
			UserDet user = commonService.getCurrentUserDetails(hrmsCode);
			List<String> roles = extractRoles(jwt);
			user.setRole(roles);

			if (user.getOfficeTypes() != null && !user.getOfficeTypes().isEmpty()) {
				user.setPostingType(user.getOfficeTypes().get(0));
			}

			return ResponseEntity.ok(ApiResponse.<UserDet>builder().status(HttpStatus.OK.value()).success(true)
					.message("Current user fetched successfully").data(user).build());
		} catch (Exception ex) {
			log.error("Error fetching current user {}: {}", hrmsCode, ex.getMessage(), ex);
			return ResponseEntity.internalServerError()
					.body(ApiResponse.<UserDet>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).success(false)
							.message("Failed to fetch current user").build());
		}
	}

	// ===========================
	// = Charge Details ==========
	// ===========================

	@GetMapping("/charge-details")
	public ResponseEntity<ApiResponse<List<ChargeDet>>> getAllCharges(HttpServletRequest request) {
		log.info("Fetching all charge details");
		return buildListResponse(commonService.fetchAllCharges(), "Charge details fetched successfully",
				"No charge details found", request);
	}

	// ===========================
	// = Circle Details ==========
	// ===========================

	@GetMapping("/circle-details")
	public ResponseEntity<ApiResponse<List<CircleDet>>> getAllCircles(HttpServletRequest request) {
		log.info("Fetching all circle details");
		return buildListResponse(commonService.fetchAllCircles(), "Circle details fetched successfully",
				"No circle details found", request);
	}

	// ===========================
	// = Office Details ==========
	// ===========================

	@GetMapping("/office-details")
	public ResponseEntity<ApiResponse<List<OfficeDet>>> getAllOffices(HttpServletRequest request) {
		log.info("Fetching all office details");
		return buildListResponse(commonService.fetchAllOffices(), "Office details fetched successfully",
				"No office details found", request);
	}

	// ===========================
	// = Project Details =========
	// ===========================

	@GetMapping("/project-details/{hrmsCd}")
	public ResponseEntity<ApiResponse<List<UserAssignProjectDet>>> getUserProjectDet(@PathVariable String hrmsCd,
			HttpServletRequest request) {
		log.info("Fetching user project details for HRMS: {}", hrmsCd);
		return buildListResponse(assignService.fetchUserProjectDet(hrmsCd), "Project details fetched successfully",
				"No project details found", request);
	}

	// ===========================
	// = Posting Details =========
	// ===========================

	@GetMapping("/posting-details/{hrmsCd}")
	public ResponseEntity<ApiResponse<List<UserAssignPostingDet>>> getUserPostingDet(@PathVariable String hrmsCd,
			HttpServletRequest request) {
		log.info("Fetching user posting details for HRMS: {}", hrmsCd);
		return buildListResponse(assignService.fetchUserPostingDet(hrmsCd), "Posting details fetched successfully",
				"No posting details found", request);
	}

	// ===========================
	// = Role Details ============
	// ===========================

	@GetMapping("/roles")
	public ResponseEntity<ApiResponse<List<RoleDet>>> getAllRoles(HttpServletRequest request) {
		log.info("Fetching all roles");
		return buildListResponse(commonService.fetchAllRoles(), "Roles fetched successfully", "No roles found",
				request);
	}

	// ===========================
	// = User Details ============
	// ===========================

	@GetMapping("/user-details")
	public ResponseEntity<ApiResponse<List<UserDet>>> getUserDet(@AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {
		String requester = jwt.getSubject();
		log.info("Fetching all user details for requester: {}", requester);
		return buildListResponse(commonService.fetchAllUserDetails(), "User details fetched successfully",
				"No user details found", request);
	}

	@GetMapping("/user-details/{hrmsCd}")
	public ResponseEntity<ApiResponse<UserDet>> getUserDetails(@PathVariable String hrmsCd,
			@AuthenticationPrincipal Jwt jwt) {
		log.info("Fetching user details for HRMS: {}", hrmsCd);
		UserDet user = commonService.getProfileDetails(hrmsCd);
		user.setRole(extractRoles(jwt).stream().map(String::toLowerCase).collect(Collectors.toList()));
		return ResponseEntity.ok(ApiResponse.success("User details fetched successfully", user));
	}

	// ===========================
	// = Assignment Handling =====
	// ===========================

	@GetMapping("/assigned")
	public ResponseEntity<ApiResponse<List<UserAssignDet>>> getAssignUsers(@AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {
		String hrmsCode = jwt.getSubject();
		String role = extractPrimaryRole(jwt);
		log.info("Fetching assigned users for approver HRMS: {} with role: {}", hrmsCode, role);

		return buildListResponse(assignService.getAllUsersWithPostingsAndProjects(hrmsCode, role),
				"Assigned users fetched successfully", "No assigned users found", request);
	}

	@GetMapping("/assigned-all")
	public ResponseEntity<ApiResponse<List<UserAssignDet>>> getAllAssignUsers(@AuthenticationPrincipal Jwt jwt,
			HttpServletRequest request) {
		String hrmsCode = jwt.getSubject();
		String role = extractPrimaryRole(jwt);
		log.info("Fetching all assigned users for approver HRMS: {} with role: {}", hrmsCode, role);

		return buildListResponse(assignService.getAllUsersWithPostings(hrmsCode, role),
				"Assigned users fetched successfully", "No assigned users found", request);
	}

	@PostMapping("/assign")
	public ResponseEntity<ApiResponse<String>> assignRolesAndProjects(@Valid @RequestBody UserAssignRequest req,
			@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
		log.info("Processing role/project assignment for HRMS: {} by {}", req.getHrmsCode(), jwt.getSubject());
		assignService.saveAllotData(req, jwt.getSubject());
		return buildSuccessResponse("Roles and projects assigned successfully", req.getHrmsCode(), request);
	}

	@PostMapping("/edit-projects")
	public ResponseEntity<ApiResponse<String>> assignAddProjects(@Valid @RequestBody UserAssignRequest req,
			@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
		log.info("Processing project update for HRMS: {} by {}", req.getHrmsCode(), jwt.getSubject());
		assignService.updateAllotData(req, jwt.getSubject());
		return buildSuccessResponse("Projects updated successfully", req.getHrmsCode(), request);
	}

	@PostMapping("/release-employee")
	public ResponseEntity<ApiResponse<String>> releaseUser(@Valid @RequestBody UserReleaseRequest req,
			@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
		log.info("Releasing employee HRMS: {} by {}", req.getHrmsCode(), jwt.getSubject());
		String msg = assignService.releaseUserDetails(req, jwt.getSubject());
		log.info(msg);

		return ResponseEntity.ok(ApiResponse.<String>builder().status(HttpStatus.OK.value()).success(true)
				.message("Employee release processed successfully.").data(req.getHrmsCode())
				.path(request.getRequestURI()).build());
	}
	
	@PostMapping("/add-module")
	public ResponseEntity<ApiResponse<String>> add_module(@Valid @RequestBody AddModuleRequest req,
			@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
		log.info("Received assignment request: {} by user {}", req, jwt.getSubject());
		try {
			assignService.addModule(req);
			return buildSuccessResponse("Project Add Successfully", jwt.getSubject(), request);
		} catch (ServiceException se) {
			log.error("Service error during add Module {}: {}",  se.getMessage(), se);
			throw se;
			
		} catch (Exception ex) {
			log.error("Unexpected error during addmodule {}: {}",  ex.getMessage(), ex);
			throw new ServiceException("Unexpected error during add Module " , ex);
			
		}
	}

	// ===========================
	// = Helper Methods ==========
	// ===========================

	private <T> ResponseEntity<ApiResponse<List<T>>> buildListResponse(List<T> list, String successMsg, String emptyMsg,
			HttpServletRequest request) {
		if (list == null || list.isEmpty()) {
			log.warn(emptyMsg);
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body(ApiResponse.<List<T>>builder().status(HttpStatus.NO_CONTENT.value()).message(emptyMsg)
							.data(Collections.emptyList()).success(true).path(request.getRequestURI()).build());
		}
		return ResponseEntity.ok(ApiResponse.<List<T>>builder().status(HttpStatus.OK.value()).message(successMsg)
				.data(list).success(true).path(request.getRequestURI()).build());
	}

	private ResponseEntity<ApiResponse<String>> buildSuccessResponse(String msg, String hrmsCode,
			HttpServletRequest request) {
		return ResponseEntity.ok(ApiResponse.<String>builder().status(HttpStatus.OK.value()).message(msg).data(hrmsCode)
				.success(true).path(request.getRequestURI()).build());
	}

	private List<String> extractRoles(Jwt jwt) {
		return jwt.getClaimAsStringList("roles").stream().map(role -> role.replaceFirst("ROLE_", ""))
				.collect(Collectors.toList());
	}

	private String extractPrimaryRole(Jwt jwt) {
		List<String> roles = extractRoles(jwt);
		return roles.isEmpty() ? null : roles.get(0);
	}
}
