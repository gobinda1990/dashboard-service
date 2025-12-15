package comtax.gov.webapp.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.ApiResponse;
import comtax.gov.webapp.model.AssetDet;
import comtax.gov.webapp.model.AuthUserDetails;
import comtax.gov.webapp.model.EmployeeCountSummary;
import comtax.gov.webapp.model.common.ChargeDet;
import comtax.gov.webapp.model.common.CircleDet;
import comtax.gov.webapp.model.common.DesignDet;
import comtax.gov.webapp.model.common.OfficeDet;
import comtax.gov.webapp.model.common.ProjectDet;
import comtax.gov.webapp.model.common.UserDet;
import comtax.gov.webapp.service.CommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/dashboard")
@Slf4j
@RequiredArgsConstructor
public class CommonController {
	
	@Value("${upload.path}")
    private String uploadPath;
	
	@Value("file.base-url")
	private String frontend_img_url;

	private final CommonService commonService;
//	private static final String UPLOAD_DIR = "/resources/uploads/profile-pics/";
	

	@GetMapping("/project-details")
	public ResponseEntity<ApiResponse<List<ProjectDet>>> getProjectDetails(@AuthenticationPrincipal Jwt jwt) {
		final String userId = jwt.getSubject();
		log.info("Fetching project details for user: {}", userId);
		try {
			AuthUserDetails authUserDetails = buildAuthUserDetails(jwt);
			List<ProjectDet> projectList = commonService.fetchAllProjects(authUserDetails);
			if (projectList == null || projectList.isEmpty()) {
				log.warn("No project details found for user: {}", userId);
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(new ApiResponse<>(204, "No project details found", Collections.emptyList()));
			}			
			return ResponseEntity
					.ok(new ApiResponse<>(HttpStatus.OK.value(), "Project details fetched successfully", projectList));

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("Error fetching project details for user: {}", userId, ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(500, "Internal server error: " + ex.getMessage(), Collections.emptyList()));
		}
	}

	/**
	 * Utility method to construct AuthUserDetails from JWT claims.
	 */
	private AuthUserDetails buildAuthUserDetails(Jwt jwt) {
		List<String> roles = jwt.getClaimAsStringList("roles");
		boolean isAdmin = roles != null && roles.contains("ROLE_ADMIN");

		return AuthUserDetails.builder().hrmsCd(jwt.getClaimAsString("hrmsCd")).emailId(jwt.getClaimAsString("emailId"))
				.circleCd(jwt.getClaimAsString("circleCd")).chargeCd(jwt.getClaimAsString("chargeCd"))
				.phoneNo(jwt.getClaimAsString("phoneNo")).gpfNo(jwt.getClaimAsString("gpfNo"))
				.panNo(jwt.getClaimAsString("panNo")).boId(jwt.getClaimAsString("boId")).role(roles).admin(isAdmin)
				.build();
	}

	
	
	
	
	
	@GetMapping("/custodian/assets")
	public ResponseEntity<ApiResponse<List<AssetDet>>> getCustodianAssets(@AuthenticationPrincipal Jwt jwt) {
	  //  List<AssetDet> assets = assetService.findByCustodian(jwt.getSubject());
	    return ResponseEntity.ok(new ApiResponse<>(200, "Assets fetched successfully", null));
	}
	


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDet>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String hrms = jwt.getSubject();

        
        UserDet user = commonService.getProfileDetails(hrms);
//        UserDet user = new UserDet(hrms, "Super Admin", "abcd@aa.com", "8653862224",
//                "Circle Officer", "GPF98765", "ABCDE1234F", "BO12345",
//                "http://localhost:8082/uploads/profile-pics/2020_keanu.jpg"
//        );

        return ResponseEntity.ok(new ApiResponse<>(200, "Profile fetched successfully", user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody UserDet updated) {

        // Normally update in DB; simulated here
        return ResponseEntity.ok(new ApiResponse<>(200, "Profile updated successfully", "OK"));
    }

    @PostMapping("/profile/upload")
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam("file") MultipartFile file) throws IOException {
    	String hrms = jwt.getSubject();
log.info("uploaded file name>>>>>"+file.getOriginalFilename());
        File dir = new File(uploadPath);
        if (!dir.exists()) dir.mkdirs();
        log.info("upload file after making dir>>>>>");
        String filename = jwt.getSubject() + "_" + file.getOriginalFilename();
        File dest = new File(uploadPath + filename);
        file.transferTo(dest);
        log.info("uploaded file complete>>>>>");
        String fileUrl = uploadPath + filename;
        commonService.uploadProfileImg(hrms,filename);
        log.info("pic uploaded successfully on>>>"+fileUrl);
        log.info("Image return url for frontend>>>>>"+"http://localhost:8082/api/uploads/profile-pics/"+filename);
        return ResponseEntity.ok(new ApiResponse<>(200, "Profile image uploaded", "http://localhost:8082/api/uploads/profile-pics/"+filename));
    }
    
  

	@GetMapping("/user-details")
	public ResponseEntity<ApiResponse<List<UserDet>>> getUserDet(@AuthenticationPrincipal Jwt jwt) {

		log.info("Enter into getUserDet for user: {}", jwt.getSubject());

		try {
			List<UserDet> userLists = commonService.fetchAllUserDetails();

			if (userLists == null || userLists.isEmpty()) {
				log.warn("No project details found for user: {}");
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(new ApiResponse<>(204, "No project details found", Collections.emptyList()));
			}
			// log.info("Project details fetched successfully for user: {}", userId);
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
			// log.info("Fetched {} circles successfully", circles.size());
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
	
	@GetMapping("/designation-details")
	public ResponseEntity<ApiResponse<List<DesignDet>>> getAllDesignations() {
		log.debug("Received request: /designation-details");

		try {
			List<DesignDet> designs = commonService.fetchAllDesignations();

			if (designs == null || designs.isEmpty()) {
				log.warn("No designation details found");
				return ResponseEntity.status(HttpStatus.NO_CONTENT)
						.body(new ApiResponse<>(204, "No designation details found", Collections.emptyList()));
			}			
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
	
	@GetMapping("/office-details")
	public ResponseEntity<ApiResponse<List<OfficeDet>>> getAllOffices() {
		log.debug("Received request: /office-details");

		try {
			List<OfficeDet> offices = commonService.fetchAllOffices();

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
	

	 @GetMapping("/reports")
	    public ResponseEntity<ApiResponse<EmployeeCountSummary>> getReportofEmployee(@AuthenticationPrincipal Jwt jwt) {
	        String hrms = jwt.getSubject();

	    
	        EmployeeCountSummary summary = commonService.getCountForReport();
	        return ResponseEntity.ok(new ApiResponse<>(200, "Report fetched successfully", summary));
	    
	     
	 }
	 
	 @GetMapping("/release-emp/{id}")
	    public ResponseEntity<ApiResponse<String>> doReleaseEmployee(@AuthenticationPrincipal Jwt jwt,@PathVariable ("id") String id) {
	        String hrms = jwt.getSubject();

	    
	        
	        String release_message = commonService.releaseEmployee(id);
	       
	        return ResponseEntity.ok(new ApiResponse<>(200, "Report fetched successfully", release_message));
	    
	     
	 }
}
