package comtax.gov.webapp.repo;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import comtax.gov.webapp.model.AllotEmployeeBean;
import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.AssignedEmpBean;
import comtax.gov.webapp.model.common.CircleDet;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AssignEmpRepoImpl implements AssignEmpRepo {

	@Autowired
	private final JdbcTemplate jdbcTemplate;

	
	public boolean saveAdditionalPosting(AssignRequest allotbean) {
		boolean isSavedFlag = false;
		int rowCountupadte = 0;
		log.info("enter save allotted project of employee");

		try {
			
			List<Object[]> batchArgs = new ArrayList<>();
			
			
			for (String officeCd : allotbean.getOfficeCds()) {
			    batchArgs.add(new Object[]{
			            allotbean.getHrmsCode(),
			            "",        
			            "",        
			            officeCd,  
			            "2020"     
			    });
			}

			int[] rowsInserted = jdbcTemplate.batchUpdate(insertAddlPostSQL, batchArgs);
		
			

			if (rowsInserted.length > 0) {
				isSavedFlag = true;
			}
		} catch (DataAccessException dae) {

			dae.printStackTrace();
			log.error("Database Error while saving allotted project!", dae.toString());

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unexpected Error while saving allotted project!", e.toString());
		}

		return isSavedFlag;
	}
	
	
	public boolean saveAllotProject(AssignRequest allotbean) {
		boolean isSavedFlag = false;
		int rowCountupadte = 0;
		log.info("enter save allotted project of employee");

		try {
			
			List<Object[]> batchArgs2 = new ArrayList<>();

			for (String projectId : allotbean.getProjectIds()) {
			    batchArgs2.add(new Object[]{
			            allotbean.getHrmsCode(),
			            projectId,     
			            "L"
			    });
			}

			int[] insertedProjects = jdbcTemplate.batchUpdate(insertAllotProjectSQL, batchArgs2);
		
			jdbcTemplate.update("update impact2_user_master set usr_status_cd='L' where hrms_code=?",allotbean.getHrmsCode());

			if (insertedProjects.length > 0) {
				isSavedFlag = true;
			}
		} catch (DataAccessException dae) {

			dae.printStackTrace();
			log.error("Database Error while saving allotted project!", dae.toString());

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unexpected Error while saving allotted project!", e.toString());
		}

		return isSavedFlag;
	}

	
	public boolean saveAllotLog(AssignRequest allotbean,String ops_tab_name,String ops_type) {
		boolean isSavedFlag = false;
		int rowCountupadte = 0;
		log.info("enter save  logs");

		try {
			rowCountupadte = jdbcTemplate.update(insertAllotOPSLogSQL,"2020",ops_tab_name,ops_type,allotbean.getUserIp(),allotbean.getHrmsCode());

			if (rowCountupadte > 0) {
				isSavedFlag = true;
			}
		} catch (DataAccessException dae) {

			dae.printStackTrace();
			log.error("Database Error while saving  logs!", dae.toString());

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unexpected Error while saving  logs!", e.toString());
		}

		return isSavedFlag;
	}



	@Override
	public boolean saveAllotData(AssignRequest allotbean) throws Exception {
		boolean isSavedFlag = false;
		int rowCountupadte = 0;
		log.info("enter save allotted employee repo"+allotbean.getHrmsCode());

		log.info("Role ID repo:"+allotbean.getRoleId());
		
	
			rowCountupadte = jdbcTemplate.update(insertAllotSQL, allotbean.getHrmsCode(), "M",
					"", allotbean.getOfficeId(),
					"2020",allotbean.getRoleId().get(0),allotbean.getChargeCd(),allotbean.getCircleCd());

			if (rowCountupadte > 0) {
		
				if(saveAllotProject(allotbean)) {
					if(saveRoleforAssign(allotbean)) {
						if(saveAllotLog(allotbean,"impact2_user_posting","I")) {
							isSavedFlag = true;
						}
						
					}
					
					
				}
				
			}
		

		return isSavedFlag;
	}


	@Override
	public List<AssignRequest> fetchAssignData()throws Exception {
		log.info("enter fetch assigned employee repoImpl");
		return jdbcTemplate.query(fetchAssigned, new UserPostingRowMapper(),"2020");
		
		
		
	}

	public boolean saveRoleforAssign(AssignRequest allotbean) {
		boolean isSavedFlag = false;
		int rowCountupadte = 0;
		log.info("enter save  logs");

		try {
			rowCountupadte = jdbcTemplate.update(insertRoleMasterSQL,allotbean.getHrmsCode(),allotbean.getRoleId().get(0));

			if (rowCountupadte > 0) {
				isSavedFlag = true;
			}
		} catch (DataAccessException dae) {

			dae.printStackTrace();
			log.error("Database Error while saving  logs!", dae.toString());

		} catch (Exception e) {
			e.printStackTrace();
			log.error("Unexpected Error while saving  logs!", e.toString());
		}

		return isSavedFlag;
	}
	

}
