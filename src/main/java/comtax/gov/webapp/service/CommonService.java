package comtax.gov.webapp.service;

import java.util.List;

import comtax.gov.webapp.model.AddModuleRequest;
import comtax.gov.webapp.model.AuthUserDetails;
import comtax.gov.webapp.model.EmployeeCountSummary;
import comtax.gov.webapp.model.RoleDet;
import comtax.gov.webapp.model.common.ChargeDet;
import comtax.gov.webapp.model.common.CircleDet;
import comtax.gov.webapp.model.common.DesignDet;
import comtax.gov.webapp.model.common.OfficeDet;
import comtax.gov.webapp.model.common.ProjectDet;
import comtax.gov.webapp.model.common.UserDet;

public interface CommonService {

	List<ProjectDet> fetchAllProjects(AuthUserDetails authUserDet);

	List<UserDet> fetchAllUserDetails();

	List<CircleDet> fetchAllCircles();

	List<ChargeDet> fetchAllCharges();

	List<DesignDet> fetchAllDesignations();

	List<RoleDet> fetchAllRoles();

	List<OfficeDet> fetchAllOffices();

	UserDet getProfileDetails(String hrms_code);

	void uploadProfileImg(String hrms, String img_url);

	EmployeeCountSummary getCountForReport();

	String releaseEmployee(String hrms);

	UserDet getCurrentUserDetails(String hrmsCd);
	
	public boolean addModule(AddModuleRequest bn);

}
