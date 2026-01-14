package comtax.gov.webapp.service;

import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import comtax.gov.webapp.exception.ServiceException;
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
import comtax.gov.webapp.repo.CommonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonServiceImpl implements CommonService {

    private final CommonRepository commonRepository;
   
    @Override
//    @Cacheable(value = "projects", key = "#authUserDet.hrmsCd", unless = "#result == null || #result.isEmpty()")
    public List<ProjectDet> fetchAllProjects(AuthUserDetails authUserDet) {
        try {
            log.debug("Fetching projects for user={}", authUserDet.getHrmsCd());
            List<ProjectDet> projects = commonRepository.fetchAllProjects(authUserDet);
            if (projects.isEmpty()) {
                log.warn("No projects found for user={}", authUserDet.getHrmsCd());
            } else {
                log.info("Fetched {} projects for user={}", projects.size(), authUserDet.getHrmsCd());
            }
            return projects;
        } catch (DataAccessException dae) {
            log.error("DB error while fetching projects for user={}", authUserDet.getHrmsCd(), dae);
            throw new ServiceException("Failed to fetch project list", dae);
        } catch (Exception ex) {
            log.error("Unexpected error fetching projects for user={}", authUserDet.getHrmsCd(), ex);
            throw new ServiceException("Unexpected error occurred while fetching project list", ex);
        }
    }

    @Override
//    @Cacheable(value = "users", unless = "#result == null || #result.isEmpty()")
    public List<UserDet> fetchAllUserDetails() {
        try {
            List<UserDet> users = commonRepository.fetchAllUserDetails();
            log.info("Fetched {} users", users.size());
            return users;
        } catch (DataAccessException dae) {
            log.error("DB error while fetching users", dae);
            throw new ServiceException("Failed to fetch users list", dae);
        }
    }

    @Override
//    @Cacheable(value = "circles", unless = "#result == null || #result.isEmpty()")
    public List<CircleDet> fetchAllCircles() {
        try {
            List<CircleDet> circles = commonRepository.fetchAllCircles();
            log.info("Fetched {} circles", circles.size());
            return circles;
        } catch (DataAccessException dae) {
            log.error("DB error while fetching circles", dae);
            throw new ServiceException("Failed to fetch circle list", dae);
        }
    }

    @Override
//    @Cacheable(value = "charges", unless = "#result == null || #result.isEmpty()")
    public List<ChargeDet> fetchAllCharges() {
        try {
            List<ChargeDet> charges = commonRepository.fetchAllCharges();
            log.info("Fetched {} charges", charges.size());
            return charges;
        } catch (DataAccessException dae) {
            log.error("DB error while fetching charges", dae);
            throw new ServiceException("Failed to fetch charges list", dae);
        }
    }

    @Override
//    @Cacheable(value = "designations", unless = "#result == null || #result.isEmpty()")
    public List<DesignDet> fetchAllDesignations() {
        try {
            List<DesignDet> designations = commonRepository.fetchAllDesignations();
            log.info("Fetched {} designations", designations.size());
            return designations;
        } catch (DataAccessException dae) {
            log.error("DB error while fetching designations", dae);
            throw new ServiceException("Failed to fetch designation list", dae);
        }
    }
	
	@Override
	public List<RoleDet> fetchAllRoles() {
		try {
			  log.info("Fetched Roles");	          
	          return commonRepository.fetchAllRoles();
	      } catch (DataAccessException dae) {
	          log.error("DB error while fetching offices", dae);
	          throw new ServiceException("Failed to fetch offices list", dae);
	      }
	}

	@Override
	public List<OfficeDet> fetchAllOffices() {
	      try {
	          List<OfficeDet> offices = commonRepository.fetchAllOffices();
	          log.info("Fetched {} offices", offices.size());
	          return offices;
	      } catch (DataAccessException dae) {
	          log.error("DB error while fetching offices", dae);
	          throw new ServiceException("Failed to fetch offices list", dae);
	      }
	  }

	@Override
	public UserDet getProfileDetails(String hrms_code) {
		 try {
	          UserDet userprofile = commonRepository.getProfileDetails(hrms_code);
	          log.info("Fetched {} user profile", userprofile.getHrmsCode());
	          return userprofile;
	      } catch (DataAccessException dae) {
	          log.error("DB error while fetching user profile", dae);
	          throw new ServiceException("Failed to fetch user profile", dae);
	      }
	}

	@Override
	@Transactional
	public void uploadProfileImg(String hrms,String img_url) {


		 try {
	          commonRepository.uploadProfileImg(hrms,img_url);
	          log.info("Saving user profile image ur> {} ", img_url);
	          
	      } catch (DataAccessException dae) {
	          log.error("DB error while saving user profile image", dae);
	          throw new ServiceException("Failed to save user profile image", dae);
	      }
		
	}

	@Override
	public EmployeeCountSummary getCountForReport() {
		
		 try {
			 EmployeeCountSummary summary =  commonRepository.getCountForReport();
	          log.info("getting report for common/assigned---"+summary.getAssignedCount()+"--"+summary.getCommonPoolCount());
	          return summary;
	          
	      } catch (DataAccessException dae) {
	          log.error("DB error while getting report for common/assigned", dae);
	          throw new ServiceException("Failed to getting report for common/assigned", dae);
	      }
		
		
	}

	@Override
	@Transactional
	public String releaseEmployee(String hrms) {
		String message="Releaseing Error";
		int row_updated = 0;
		 try {
			 row_updated =  commonRepository.releaseEmployee(hrms);
			
	          log.info("in release service");
	          if(row_updated>0) {
	        	  message = "Employee :"+hrms+" released successfully";
	          }
	          return message;
	          
	      } catch (DataAccessException dae) {
	          log.error("DB error while releasing employee", dae);
	          throw new ServiceException("Failed to releasing employee", dae);
	      }
	}

	@Override
	public UserDet getCurrentUserDetails(String hrmsCd) {		
		return commonRepository.fetchCurrentUserDetails(hrmsCd);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public boolean addModule(AddModuleRequest bn) {		
	
		log.info("Starting addModule");
		try {
		
			boolean addmodule = commonRepository.addModule(bn);
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

}

