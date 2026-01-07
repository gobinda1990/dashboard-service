package comtax.gov.webapp.service;

import java.util.List;

import comtax.gov.webapp.model.AddModuleRequest;
import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.UserAssignDet;
import comtax.gov.webapp.model.UserAssignPostingDet;
import comtax.gov.webapp.model.UserAssignProjectDet;
import comtax.gov.webapp.model.UserAssignRequest;
import comtax.gov.webapp.model.UserReleaseRequest;

public interface AssignService {

	public boolean saveAllotData(UserAssignRequest allotbean, String AssignHrmsCd);
	
	public boolean updateAllotData(UserAssignRequest allotbean, String AssignHrmsCd);

	public List<AssignRequest> fetchAssignData(String assignHrmsCd);

	public List<AssignRequest> getAllAssignRequests();
	
	public AssignRequest fetchUserDetails(String hrmsCd);
	
	public List<UserAssignPostingDet> fetchUserPostingDet(String hrmsCd);
	
	public List<UserAssignProjectDet> fetchUserProjectDet(String hrmsCd);
	
	public List<UserAssignDet> getAllUsersWithPostingsAndProjects(String hrmsCd,String role) ;
     
	public String releaseUserDetails(UserReleaseRequest userRelReq,String hrmsCd);
	
	public List<UserAssignDet> getAllUsersWithPostings(String hrmsCd, String role);
	
	public boolean addModule(AddModuleRequest bn);
	
    public List<UserAssignDet> getAllUsersWithPostingsAndProjects(String hrmsCd,String role,String postingType) ;
	
	public List<UserAssignDet> getAssignedUserAM(String hrmsCd,String role) ;

}
