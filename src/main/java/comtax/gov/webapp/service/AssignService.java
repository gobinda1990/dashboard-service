package comtax.gov.webapp.service;

import java.util.List;

import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.AssignedEmpBean;

public interface AssignService {
	
	public boolean saveAllotData(AssignRequest allotbean);
	
	public List<AssignRequest> fetchAssignData();
	

}
