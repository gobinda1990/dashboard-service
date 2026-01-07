package comtax.gov.webapp.service;

import comtax.gov.webapp.model.AddModuleRequest;
import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.ModuleRow;
import comtax.gov.webapp.model.ReleaseModuleRequest;
import comtax.gov.webapp.model.ReleasePostingRequest;
import comtax.gov.webapp.model.UserAssignDet;
import comtax.gov.webapp.model.UserAssignPostingDet;
import comtax.gov.webapp.model.UserAssignProjectDet;
import comtax.gov.webapp.model.UserAssignRequest;
import comtax.gov.webapp.model.UserReleaseRequest;
import comtax.gov.webapp.repo.AssignEmpRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignServiceImpl implements AssignService {

	private final AssignEmpRepo assignEmpRepo;

	/**
	 * Save all related assignment data transactionally.
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean saveAllotData(UserAssignRequest request, String assignHrmsCd) {
		log.info("Starting employee assignment for HRMS: {}", request.getHrmsCode());
		try {
			// Step 1: Save primary posting record
			boolean savedPosting = assignEmpRepo.saveUserPostingData(request, assignHrmsCd);
			if (!savedPosting) {
				log.warn("Primary posting not saved for HRMS {}", request.getHrmsCode());
				return false;
			}
			assignEmpRepo.saveUserModulesData(request, assignHrmsCd);
			
			String roleIdForProject1 = null;

			for (ModuleRow module : request.getModules()) {
			    if ("1".equals(module.getProjectId())) { 
			        roleIdForProject1 = module.getRoleId();
			        break; // stop after first match
			    }
			}
			// Step 3: Save role mapping
			 assignEmpRepo.saveRoleForAssign(request.getHrmsCode(),roleIdForProject1);
			// Step 4: Save audit log
			// assignEmpRepo.saveAllotLog(request, "impact2_user_posting",
			// "I",assignHrmsCd);

			log.info("Successfully saved all assignment data for HRMS {}", request.getHrmsCode());
			return true;

		} catch (DataAccessException dae) {
			log.error("Database error during saveAllotData for HRMS {}", request.getHrmsCode(), dae);
			throw dae; // Triggers rollback
		} catch (Exception e) {
			log.error("Unexpected error during saveAllotData for HRMS {}", request.getHrmsCode(), e.getMessage());
			throw new RuntimeException(e.getMessage()); // Triggers rollback
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean updateAllotData(UserAssignRequest request, String assignHrmsCd) {
		log.info("Starting employee assignment for HRMS updateAllotData: {}", request.getHrmsCode());
		try {
			if(request.getPostings().size()>0)
			 
				assignEmpRepo.saveAdditionalPosting(request, assignHrmsCd);
			if (request.getModules().size() > 0)
				assignEmpRepo.saveUserModulesData(request, assignHrmsCd);			

			log.info("Successfully saved all assignment data for HRMS {}", request.getHrmsCode());
			return true;

		} catch (DataAccessException dae) {
			log.error("Database error during saveAllotData for HRMS {}", request.getHrmsCode(), dae);
			throw dae; // Triggers rollback
		} catch (Exception e) {
			log.error("Unexpected error during saveAllotData for HRMS {}", request.getHrmsCode(), e.getMessage());
			throw new RuntimeException(e.getMessage()); // Triggers rollback
		}
	}

	/**
	 * Fetch assigned employees safely.
	 */
	@Override
	public List<AssignRequest> fetchAssignData(String assignHrmsCd) {
		log.info("Fetching assigned employee data");
		try {
			return assignEmpRepo.fetchAssignData(assignHrmsCd);
		} catch (DataAccessException dae) {
			log.error("Database error fetching assigned employee data", dae);
		} catch (Exception e) {
			log.error("Unexpected error fetching assigned employee data", e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<AssignRequest> getAllAssignRequests() {
		log.debug("Fetching all assignment requests (not yet implemented)");
		return Collections.emptyList();
	}

	@Override
	public AssignRequest fetchUserDetails(String hrmsCd) {
		log.info("Enter into fetchUserDetails:--");

		return null;
	}

	@Override
	public List<UserAssignPostingDet> fetchUserPostingDet(String hrmsCd) {
		log.info("Fetching assigned employee data");
		try {
			return assignEmpRepo.fetchUserPostingDet(hrmsCd);
		} catch (DataAccessException dae) {
			log.error("Database error fetching assigned employee data", dae);
		} catch (Exception e) {
			log.error("Unexpected error fetching assigned employee data", e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<UserAssignProjectDet> fetchUserProjectDet(String hrmsCd) {
		log.info("Fetching assigned employee data");
		try {
			return assignEmpRepo.fetchUserProjectDet(hrmsCd);
		} catch (DataAccessException dae) {
			log.error("Database error fetching assigned employee data", dae);
		} catch (Exception e) {
			log.error("Unexpected error fetching assigned employee data", e);
		}
		return Collections.emptyList();
	}

	@Override
	public List<UserAssignDet> getAllUsersWithPostingsAndProjects(String hrmsCd, String role) {
		log.info("Fetching assigned employee data");
		return assignEmpRepo.getAllUsersWithPostingsAndProjects(hrmsCd, role);
	}
	
	@Override
	public List<UserAssignDet> getAllUsersWithPostings(String hrmsCd, String role) {
		log.info("Enter into getAllUsersWithPostings:--");
		return assignEmpRepo.getAllUsersWithPostings(hrmsCd, role);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String releaseUserDetails(UserReleaseRequest userRelReq, String hrmsCd) {
	    log.info("Enter into releaseUserDetails(): hrmsCd={}, requestHrmsCd={}", hrmsCd, userRelReq.getHrmsCode());

	    try {
	        boolean postingsUpdated = false;
	        boolean projectsUpdated = false;
	        boolean statusUpdated = false;

	        // --- 1️⃣ Fetch posting count ---
	        int count = assignEmpRepo.fetchUserPostingCount(userRelReq.getHrmsCode());
          //  log.info("",count);
	        // --- 2️⃣ Release Posting Data ---
	        List<ReleasePostingRequest> postings = userRelReq.getReleasePostings();
	        if (postings != null && !postings.isEmpty()) {
	            postingsUpdated = assignEmpRepo.releaseUserPostingData(userRelReq, hrmsCd);
	            log.info("User posting data released: {}", postingsUpdated);
	        } else {
	            log.info("No posting records to release for HRMS: {}", userRelReq.getHrmsCode());
	        }

	        // --- 3️⃣ Release Project/Module Data ---
	        List<ReleaseModuleRequest> projects = userRelReq.getReleaseProjects();
	        if (projects != null && !projects.isEmpty()) {
	            projectsUpdated = assignEmpRepo.releaseUserModulesData(userRelReq, hrmsCd);
	            log.info("User module data released: {}", projectsUpdated);
	        } else {
	            log.info("No module records to release for HRMS: {}", userRelReq.getHrmsCode());
	        }

	        //  Update user status if all postings released ---
	        if (postings != null && count == postings.size()) {
	            statusUpdated = assignEmpRepo.updateUserStatus("A", userRelReq.getHrmsCode());
	            log.info("User master status update result: {}", statusUpdated);

	            if (statusUpdated) {
	                log.info("User release completed successfully for HRMS: {}", userRelReq.getHrmsCode());
	                return "User release completed successfully.";
	            } else {
	                log.warn("User release completed with warnings for HRMS: {}", userRelReq.getHrmsCode());
	                return "User release partially completed (status not updated).";
	            }
	        } else {
	            log.info("Posting count mismatch (DB count={}, request size={}) for HRMS: {}", count,
	                    (postings != null ? postings.size() : 0), userRelReq.getHrmsCode());
	            return "User release skipped or incomplete (count mismatch).";
	        }

	    } catch (Exception e) {
	        log.error("Error while releasing user details for HRMS: {}",
	                userRelReq != null ? userRelReq.getHrmsCode() : hrmsCd, e);
	        throw new RuntimeException("Error releasing user details: " + e.getMessage(), e);
	    }
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean addModule(AddModuleRequest bn) {		
	
		log.info("Starting addModule");
		try {
		
			boolean addmodule = assignEmpRepo.addModule(bn);
			if(!addmodule) {
				log.warn("Primary posting not saved for HRMS {}");
				return false;
			}else {
				return true;
			}
		
			} catch (Exception e) {
				log.error("Unexpected error during saveAllotData for HRMS {}", e.getMessage());
				throw new RuntimeException(e.getMessage()); // Triggers rollback
			}
		
		
	}

	@Override
	public List<UserAssignDet> getAllUsersWithPostingsAndProjects(String hrmsCd, String role,String postingType) {
		log.info("Fetching assigned employee data");
		return assignEmpRepo.getAllUsersWithPostingsAndProjects(hrmsCd, role,postingType);
	}
	@Override
	public List<UserAssignDet> getAssignedUserAM(String hrmsCd, String role) {
		log.info("Fetching assigned employee data");
		return assignEmpRepo.getAssignedUserAM(hrmsCd, role);
	}


}
