package comtax.gov.webapp.model;

import jakarta.validation.constraints.NotBlank;

public class AllotEmployeeBean {
	
	@NotBlank(message = "HRMS code is rerquired")
	private String hrmsCode;
	@NotBlank(message = "postingType code is rerquired")
    private String postingType;
	@NotBlank(message = "officeType code is rerquired")
    private String officeType;
	
	@NotBlank(message = "officeCd code is rerquired")
    private String officeCd;
	
    private String activeDt;
	
    private String inactiveDt;
	
    private String status;
	@NotBlank(message = "approverHrms code is rerquired")
    private String approverHrms;
	
    private String logDt;
	
    private String projectId;
	@NotBlank(message = "Role id is required")
	private String role_id;
	@NotBlank(message = "charge_cd code is rerquired")
	private String charge_cd;
	@NotBlank(message = "circle_cd code is rerquired")
	private String circle_cd;
	
	private String userIp;

}
