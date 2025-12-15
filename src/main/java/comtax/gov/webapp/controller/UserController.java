package comtax.gov.webapp.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.ApiResponse;
import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.AssignedEmpBean;
import comtax.gov.webapp.model.PostingDet;
import comtax.gov.webapp.model.RoleDet;
import comtax.gov.webapp.model.common.ChargeDet;
import comtax.gov.webapp.model.common.CircleDet;
import comtax.gov.webapp.model.common.OfficeDet;
import comtax.gov.webapp.model.common.UserDet;
import comtax.gov.webapp.service.AssignService;
import comtax.gov.webapp.service.CommonService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

	private final CommonService commonService;
	
	private final AssignService asignservice;

	@GetMapping("/user-details")
	public ResponseEntity<ApiResponse<List<UserDet>>> getUserDet(@AuthenticationPrincipal Jwt jwt) {

		log.info("Enter into getUserDet for user: {}", jwt.getSubject());

		try {
			List<UserDet> userLists = commonService.fetchAllUserDetails();

			if (userLists == null || userLists.isEmpty()) {
				log.warn("No project details found for user: {}");
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(new ApiResponse<>(204, "No user details found", Collections.emptyList()));
			}
			return ResponseEntity
					.ok(new ApiResponse<>(HttpStatus.OK.value(), "User details fetched successfully", userLists));

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error fetching users details for user: {}", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(500, "Internal server error: " + ex.getMessage(), Collections.emptyList()));
		}
	}

	@GetMapping("/circle-details")
	public ResponseEntity<ApiResponse<List<CircleDet>>> getAllCircles() {
		log.debug("Received request: /circle-details");

		try {
			List<CircleDet> circles = commonService.fetchAllCircles();

			if (circles == null || circles.isEmpty()) {
				log.warn("No circle details found");
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(new ApiResponse<>(204, "No circle details found", Collections.emptyList()));
			}
			return ResponseEntity
					.ok(new ApiResponse<>(HttpStatus.OK.value(), "Circle details fetched successfully", circles));

		} catch (ServiceException se) {
			log.error("Service error while fetching circles", se.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500,
					"Failed to fetch circle details: " + se.getMessage(), Collections.emptyList()));

		} catch (Exception ex) {
			log.error("Unexpected error while fetching circles", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					new ApiResponse<>(500, "Unexpected server error: " + ex.getMessage(), Collections.emptyList()));
		}
	}

	@GetMapping("/charge-details")
	public ResponseEntity<ApiResponse<List<ChargeDet>>> getAllCharges() {
		log.debug("Received request: /charge-details");

		try {
			List<ChargeDet> charges = commonService.fetchAllCharges();

			if (charges == null || charges.isEmpty()) {
				log.warn("No charge details found");
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(new ApiResponse<>(204, "No charge details found", Collections.emptyList()));
			}
			return ResponseEntity
					.ok(new ApiResponse<>(HttpStatus.OK.value(), "charge details fetched successfully", charges));

		} catch (ServiceException se) {
			log.error("Service error while fetching charge", se.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500,
					"Failed to fetch charge details: " + se.getMessage(), Collections.emptyList()));

		} catch (Exception ex) {
			log.error("Unexpected error while fetching charge", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					new ApiResponse<>(500, "Unexpected server error: " + ex.getMessage(), Collections.emptyList()));
		}
	}

	@GetMapping("/postings")
	public ResponseEntity<ApiResponse<List<PostingDet>>> getPostings(@AuthenticationPrincipal Jwt jwt) {
		try {
//	            List<PostingDet> postings = commonService.fetchAllPostings();
			List<PostingDet> dummyPostings = Arrays.asList(new PostingDet(1, "Head Office - Delhi"),
					new PostingDet(2, "Regional Office - Mumbai"), new PostingDet(3, "Circle Office - Chennai"),
					new PostingDet(4, "District Office - Kolkata"));
			return ResponseEntity.ok(new ApiResponse<>(200, "Postings fetched successfully", dummyPostings));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(500, "Failed to fetch postings", Collections.emptyList()));
		}
	}

	@GetMapping("/roles")
	public ResponseEntity<ApiResponse<List<RoleDet>>> getAllRoles() {
		log.debug("Received request: /roles");

		try {
			List<RoleDet> dummyPostings = commonService.fetchAllRoles();
			
			return ResponseEntity.ok(new ApiResponse<>(200, "Roles fetched successfully", dummyPostings));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(500, "Failed to fetch postings", Collections.emptyList()));
		}
	}

	@GetMapping("/office-details")
	public ResponseEntity<ApiResponse<List<OfficeDet>>> getAllOffices() {
		log.debug("Received request: /office-details");

		try {
			List<OfficeDet> offices = commonService.fetchAllOffices();
			log.info("" + offices.size());
			if (offices == null || offices.isEmpty()) {
				log.warn("No charge details found");
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(new ApiResponse<>(204, "No officee details found", Collections.emptyList()));
			}
			return ResponseEntity
					.ok(new ApiResponse<>(HttpStatus.OK.value(), "office details fetched successfully", offices));

		} catch (ServiceException se) {
			log.error("Service error while fetching office", se.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500,
					"Failed to fetch office details: " + se.getMessage(), Collections.emptyList()));

		} catch (Exception ex) {
			log.error("Unexpected error while fetching office", ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
					new ApiResponse<>(500, "Unexpected server error: " + ex.getMessage(), Collections.emptyList()));
		}
	}

	@PostMapping("/assign")
	public ResponseEntity<ApiResponse<String>> assignRolesAndProjects(@RequestBody AssignRequest req,HttpServletRequest request) {
		// userService.assignRolesAndProjects(req);
		log.info("AssignRequest" + req);
		
		boolean isalloted = false;
		String user_ip=null;
		
		log.info("Enter AllotEmployee Controller"+req.getHrmsCode());
		
		try {
////			get user ip
			user_ip = request.getHeader("X-Forwarded-For");
			 if (user_ip == null || user_ip.isEmpty() || "unknown".equalsIgnoreCase(user_ip)) {
				 user_ip = request.getRemoteAddr();   // direct IP if not behind proxy
			    }
			 req.setUserIp(user_ip);
			isalloted = asignservice.saveAllotData(req);
			
			if(!isalloted) {
				
				log.warn("Saving failed for Alloted Employee"+req.getHrmsCode());
				return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse<>(409, "Saving failed for Assigning Employee-",req.getHrmsCode()));
			}
			else {
				log.info(req.getHrmsCode()+" Employee has been Alloted Successfully");
				return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(201, "Employee has been Assigned Successfully",req.getHrmsCode()));
				
			}
		}
		catch(ServiceException se) {
			se.printStackTrace();
			log.error("Alloted Data Saving Error for"+req.getHrmsCode(),se);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(500,"Alloted Data Saving Error for",req.getHrmsCode()));
			
		}
		
		
	
	}

	@GetMapping("/assigned")
	public ResponseEntity<ApiResponse> getAssignUsers() {
		List<AssignRequest> list = new ArrayList<>();
		
		//List list = new ArrayList();

//		list.add(new AssignRequest("HR001", "Test User","Admin","Test User", "9051788852", "Circle Officer", "NA", "beliaghata",
//				Arrays.asList("R1","super admin"), 
//				Arrays.asList("Income Tax Portal", "GST Portal"),
//				Arrays.asList("kolkata", "medinipur"), null, null, null, null, null, null, null));
//
//		list.add(new AssignRequest("HR002","Test User","Admin", "Test User", "9051788852", "Circle Officer", "NA", "beliaghata",
//				Arrays.asList("R1","Admin Charge"),
//				Arrays.asList("Income Tax Portal", "GST Portal"),
//				Arrays.asList("kolkata", "medinipur"), null, null, null, null, null, null, null));
//		
		list = asignservice.fetchAssignData();
//		for(int i=0;i<list.size();i++) {
//			log.info("assigned pic>>>>"+list.get(i).getImageurl());
//		}
		log.info(list.toString()+" Assigned User fetching...");
		
		return ResponseEntity.ok(new ApiResponse<>(200, "Assigned users fetched successfully", list));
	}
}
