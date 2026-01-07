package comtax.gov.webapp.model;

import java.util.List;

public class UserDetHistory {
	
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

    // Getters and Setters

    public String getHrmsCode() { return hrmsCode; }
    public void setHrmsCode(String hrmsCode) { this.hrmsCode = hrmsCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getChargeCd() { return chargeCd; }
    public void setChargeCd(String chargeCd) { this.chargeCd = chargeCd; }

    public String getCircleCd() { return circleCd; }
    public void setCircleCd(String circleCd) { this.circleCd = circleCd; }

    public String getOfficeId() { return officeId; }
    public void setOfficeId(String officeId) { this.officeId = officeId; }

    public List<String> getRoleId() { return roleId; }
    public void setRoleId(List<String> roleId) { this.roleId = roleId; }

    public List<String> getProjectIds() { return projectIds; }
    public void setProjectIds(List<String> projectIds) { this.projectIds = projectIds; }

    public List<String> getOfficeCds() { return officeCds; }
    public void setOfficeCds(List<String> officeCds) { this.officeCds = officeCds; }

    public String getApproverHrms() { return approverHrms; }
    public void setApproverHrms(String approverHrms) { this.approverHrms = approverHrms; }

    public String getUserIp() { return userIp; }
    public void setUserIp(String userIp) { this.userIp = userIp; }

    public String getCharge_name() { return charge_name; }
    public void setCharge_name(String charge_name) { this.charge_name = charge_name; }

    public String getCircle_name() { return circle_name; }
    public void setCircle_name(String circle_name) { this.circle_name = circle_name; }

    public String getOffice_name() { return office_name; }
    public void setOffice_name(String office_name) { this.office_name = office_name; }

    public List<String> getProjectNames() { return projectNames; }
    public void setProjectNames(List<String> projectNames) { this.projectNames = projectNames; }

    public String getMain_posting() { return main_posting; }
    public void setMain_posting(String main_posting) { this.main_posting = main_posting; }

    public String getOffice_type() { return office_type; }
    public void setOffice_type(String office_type) { this.office_type = office_type; }

    public String getImageurl() { return imageurl; }
    public void setImageurl(String imageurl) { this.imageurl = imageurl; }

    public String getRole_name() { return role_name; }
    public void setRole_name(String role_name) { this.role_name = role_name; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getBo_id() { return bo_id; }
    public void setBo_id(String bo_id) { this.bo_id = bo_id; }

}
