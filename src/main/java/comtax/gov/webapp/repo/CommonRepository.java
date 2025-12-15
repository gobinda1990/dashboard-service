package comtax.gov.webapp.repo;

import java.util.List;

import comtax.gov.webapp.model.AuthUserDetails;
import comtax.gov.webapp.model.EmployeeCountSummary;
import comtax.gov.webapp.model.RoleDet;
import comtax.gov.webapp.model.common.ChargeDet;
import comtax.gov.webapp.model.common.CircleDet;
import comtax.gov.webapp.model.common.DesignDet;
import comtax.gov.webapp.model.common.OfficeDet;
import comtax.gov.webapp.model.common.ProjectDet;
import comtax.gov.webapp.model.common.UserDet;

public interface CommonRepository {
	
	String FETCH_PROJECT_SQL = "SELECT project_id, project_name, project_url FROM impact2_project_details";
	
	String FETCH_PROJECT_SQL_user = "SELECT  pm.project_id, pd.project_name, pd.project_url FROM impact2_user_project_mapping pm JOIN "
			+ "impact2_project_details pd ON pm.project_id = pd.project_id JOIN impact2_user_posting up ON pm.hrms_code = up.hrms_code "
			+ "WHERE pm.hrms_code = ? AND up.role_id = ?";
	
	
	
//    String FETCH_USER_SQL="SELECT HRMS_CODE,FULL_NAME,EMAIL,PHONE_NO,DESIG_CD,GPF_NO,PAN_NO,BO_ID FROM  IMPACT2_USER_MASTER where USR_STATUS_CD=?";
	String FETCH_USER_SQL="SELECT im.HRMS_CODE,im.FULL_NAME,EMAIL,im.PHONE_NO,(select designation from designation_cd where desig_cd=im.desig_cd) desigCd,im.GPF_NO,im.PAN_NO,im.BO_ID FROM  IMPACT2_USER_MASTER im where USR_STATUS_CD=?";
	String FETCH_CIRCLE_SQL = "SELECT circle_cd, circle_nm FROM circle_cd";
	String FETCH_CHARGE_SQL = "SELECT charge_cd, charge_nm FROM charge_cd";
	String FETCH_OFFICE_SQL = "SELECT office_cd, office_nm FROM office_cd";
	String FETCH_DESIGNATION_SQL = "SELECT desig_cd, designation FROM designation_cd";
	
	String FETCH_ROLE_SQL="SELECT role_id,role_name FROM impact2_role_master order by role_id asc";
	
	String FETCH_PROFILE_SQL = "SELECT um.hrms_code AS hrmsCode, um.full_name AS fullName, um.email AS email, um.phone_no AS phoneNo, um.desig_cd AS desigCd, um.gpf_no AS gpfNo, um.pan_no AS panNo, um.bo_id AS boId, pi.image_url AS profileImageUrl FROM impact2_user_master um LEFT JOIN impact2_profile_image pi ON um.hrms_code = pi.hrms_code AND pi.status = 'L' WHERE um.hrms_code = ?";
	
	String INSERT_PROFILE_IMG_URL_SQL = "INSERT INTO impact2_profile_image (hrms_code, image_url, image_upload_dt, status) VALUES (?, ?, CURRENT_TIMESTAMP, ?)";

	String REPORT_COUNT_SQL = "SELECT COUNT(*) FILTER (WHERE usr_status_cd = 'L') AS assigned_count, COUNT(*) FILTER (WHERE usr_status_cd = 'A') AS common_pool_count FROM impact2_user_master";
	
	String RELEASE_EMP_SQL1 = "UPDATE impact2_user_master SET usr_status_cd='A' WHERE hrms_code=?";
	String RELEASE_EMP_SQL2 = "UPDATE impact2_user_posting SET status='A' WHERE hrms_code=?";
	
	List<ProjectDet> fetchAllProjects(AuthUserDetails authUserDet);
	
	List<UserDet> fetchAllUserDetails();

	List<CircleDet> fetchAllCircles();

	List<ChargeDet> fetchAllCharges();
	
	List<OfficeDet> fetchAllOffices();

	List<DesignDet> fetchAllDesignations();
	
	List<RoleDet> fetchAllRoles();

	UserDet getProfileDetails(String hrms_code);
	
	void uploadProfileImg(String hrms,String img_url);
	
	EmployeeCountSummary getCountForReport();
	
	int releaseEmployee(String hrms);
}
