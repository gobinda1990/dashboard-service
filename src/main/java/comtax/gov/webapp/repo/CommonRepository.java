package comtax.gov.webapp.repo;

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

public interface CommonRepository {
	
	String FETCH_PROJECT_SQL = "SELECT project_id, project_name, project_url FROM impact2_project_details";
	
	String FETCH_PROJECT_SQL_user = " SELECT  pm.project_id, pd.project_name, pd.project_url FROM impact2_user_project_det pm "
			+ " LEFT JOIN impact2_project_details pd ON pm.project_id = pd.project_id WHERE pm.hrms_code = ? ";
			
	
	
	
//    String FETCH_USER_SQL="SELECT HRMS_CODE,FULL_NAME,EMAIL,PHONE_NO,DESIG_CD,GPF_NO,PAN_NO,BO_ID FROM  IMPACT2_USER_MASTER where USR_STATUS_CD=?";
	String FETCH_USER_SQL="SELECT im.HRMS_CODE,im.FULL_NAME,EMAIL,im.PHONE_NO,(select designation from designation_cd where desig_cd=im.desig_cd) desigCd,im.GPF_NO,im.PAN_NO,im.BO_ID FROM  IMPACT2_USER_MASTER im where USR_STATUS_CD=?";
	
	String SELECT_USER_ALL="SELECT  um.hrms_code AS hrmsCode,um.full_name AS fullName,um.email AS email,um.phone_no AS phoneNo,"
			+ " d.designation  AS desigName,um.gpf_no AS gpfNo,um.pan_no AS panNo,um.bo_id AS boId,upi.image_url AS profileImageUrl"
			+ " FROM impact2_user_master um "
			+ " LEFT JOIN designation_cd d ON um.desig_cd = d.desig_cd "
			+ " LEFT JOIN impact2_profile_image upi ON um.hrms_code = upi.hrms_code "
			+ " WHERE um.usr_status_cd = ? ";
	
	String FETCH_CIRCLE_SQL = "SELECT circle_cd, circle_nm FROM circle_cd";
	String FETCH_CHARGE_SQL = "SELECT charge_cd, charge_nm FROM charge_cd";
	String FETCH_OFFICE_SQL = "SELECT office_cd, office_nm FROM office_cd";
	String FETCH_DESIGNATION_SQL = "SELECT desig_cd, designation FROM designation_cd";
	
	String FETCH_ROLE_SQL="SELECT role_id,role_name FROM impact2_role_master order by role_id asc";
	
	String FETCH_PROFILE_SQL = "SELECT um.hrms_code AS hrmsCode, um.full_name AS fullName, um.email AS email, um.phone_no AS phoneNo, um.desig_cd AS desigCd, um.gpf_no AS gpfNo, um.pan_no AS panNo, um.bo_id AS boId, pi.image_url AS profileImageUrl FROM impact2_user_master um LEFT JOIN impact2_profile_image pi ON um.hrms_code = pi.hrms_code AND pi.status = 'L' WHERE um.hrms_code = ?";
	
	String UPSERT_PROFILE_IMG_URL_SQL = " INSERT INTO impact2_profile_image (hrms_code, image_url, image_upload_dt, status) "
			+ " VALUES (?, ?, CURRENT_TIMESTAMP, ?) "
			+ " ON CONFLICT (hrms_code) "
			+ " DO UPDATE SET "
			+ " image_url = EXCLUDED.image_url, image_upload_dt = CURRENT_TIMESTAMP, status = EXCLUDED.status";
	
	String REPORT_COUNT_SQL = "SELECT COUNT(*) FILTER (WHERE usr_status_cd = 'L') AS assigned_count, COUNT(*) FILTER (WHERE usr_status_cd = 'A') AS common_pool_count FROM impact2_user_master";
	
	String RELEASE_EMP_SQL1 = "UPDATE impact2_user_master SET usr_status_cd='A' WHERE hrms_code=?";
	String RELEASE_EMP_SQL2 = "UPDATE impact2_user_posting SET status='A' WHERE hrms_code=?";
	
	String SELECT_USER_DET=" SELECT  um.hrms_code AS hrmsCode,um.full_name AS fullName,um.email AS email,um.phone_no AS phoneNo, "
			+ " d.designation AS desigName,um.gpf_no AS gpfNo,um.pan_no AS panNo,um.bo_id AS boId,upi.image_url AS profileImageUrl"
			+ " FROM impact2_user_master um "
			+ " LEFT JOIN designation_cd d ON um.desig_cd = d.desig_cd "
			+ " LEFT JOIN impact2_profile_image upi ON um.hrms_code = upi.hrms_code "
			+ " WHERE um.hrms_code = ? ";
	
	 String SQL_USER =
            "SELECT hrms_code, full_name FROM impact2_user_master WHERE hrms_code = ?";
     final String SQL_OFFICE_TYPES =
            "SELECT DISTINCT office_type FROM impact2_user_posting_det WHERE hrms_code = ? AND posting_type = ? and status=? ";
     final String SQL_CIRCLE =
            "SELECT DISTINCT office_cd FROM impact2_user_posting_det WHERE hrms_code = ? AND office_type = 'CI' AND posting_type = ? and status=?";
     final String SQL_CHARGE =
            "SELECT DISTINCT office_cd FROM impact2_user_posting_det WHERE hrms_code = ? AND office_type = 'CH' AND posting_type = ? and status=?";
     final String SQL_OFFICE =
            "SELECT DISTINCT office_cd FROM impact2_user_posting_det WHERE hrms_code = ? AND office_type = 'OF' AND posting_type = ? and status=?";
    final String SQL_CHARGE_BY_CIRCLE_TEMPLATE =
            "SELECT DISTINCT charge_cd FROM charge_cd WHERE circle_cd IN (%s)";
    
    String INSERT_PROJECT = "INSERT INTO impact2_project_details"
			+ "(project_id, project_name, project_url)"
			+ "VALUES"
			+ "(nextval('impact2_project_seq'), ?, ?)";
	
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
	
	UserDet fetchCurrentUserDetails(String hrmsCd);
	
	public boolean addModule(AddModuleRequest bn);
}
