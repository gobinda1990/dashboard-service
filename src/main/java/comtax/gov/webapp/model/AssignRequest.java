package comtax.gov.webapp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignRequest {
//	private String hrmsCode; 
//	private String fullName;
//	private String assignedBy;
//    private String email;
//    private String phoneNo;
//    private String chargeCd;        
//    private String circleCd;
//    private String officeId;
//    private List<String> roleid;             
//    private List<String> projectIds;   
//    private List<String> officeCds;
//    private String approverHrms;
//    private String userIp;
//    private String charge_name;
//    private String circle_name;
//    private String office_name;
//    private List<String> projectNames;
//    private String main_posting;
	
	 private String hrmsCode;
	    private String fullName;
	    private String assignedBy;
	    private String email;
	    private String phoneNo;
	    private String chargeCd;
	    private String circleCd;
	    private String officeId;

	    private List<String> roleId;
	    private List<String> projectIds;
	    private List<String> officeCds;
	    

	    private String approverHrms;
	    private String userIp;

	    private String charge_name;
	    private String circle_name;
	    private String office_name;

	    private List<String> projectNames;

	    private String main_posting;
	    private String office_type;
	    private String imageurl;
	    private String role_name;
	    private String designation;
	    private String bo_id;



}
