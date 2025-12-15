package comtax.gov.webapp.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.AssignedEmpBean;
import comtax.gov.webapp.repo.AssignEmpRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignServiceImpl  implements AssignService{
	
	
	@Autowired
	private final AssignEmpRepo allotemprepo; 
	
	

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveAllotData(AssignRequest allotbean) {
		boolean isSavedFlag = false;
		int rowCountupadte = 0;
		log.info("enter save allotted employee service");

		try {
			isSavedFlag = allotemprepo.saveAllotData(allotbean);
		} catch (DataAccessException dae) {
			isSavedFlag =false;
			dae.printStackTrace();
			log.error("Database Error while saving !", dae.toString());

		} catch (Exception e) {
			isSavedFlag =false;
			e.printStackTrace();
			log.error("Unexpected Error while saving !", e.toString());
		}

		return isSavedFlag;
	}



	@Override
	public List<AssignRequest> fetchAssignData() {
		
		List<AssignRequest> assignData= new ArrayList<>();
		
		
		
		log.info("enter fetch assigned employee service");

		try {
			assignData = allotemprepo.fetchAssignData();
		} catch (DataAccessException dae) {
			
			dae.printStackTrace();
			log.error("Database Error while fetching employee !", dae.toString());

		} catch (Exception e) {
			
			e.printStackTrace();
			log.error("Unexpected Error while fetching employee !", e.toString());
		}
		
		return assignData;
	}



	


	

}
