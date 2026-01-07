package comtax.gov.webapp.repo;

import java.util.List;
import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.UserAssignDet;
import comtax.gov.webapp.model.UserAssignPostingDet;
import comtax.gov.webapp.model.UserAssignProjectDet;
import comtax.gov.webapp.model.UserAssignRequest;
import comtax.gov.webapp.model.UserReleaseRequest;

public interface AssignEmpRepo {

	String insertAllotSQL = "INSERT INTO impact2_user_posting (hrms_code,posting_type,office_type,office_cd,active_dt,"
			+ " status,approver_hrms,log_dt,role_id,charge_cd,circle_cd) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'L', ?, "
			+ " CURRENT_TIMESTAMP,?,?,?)";

	String insertAllotProjectSQL = "INSERT INTO impact2_user_project_mapping "
			+ "(hrms_code, project_id, status, log_dt) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";

	String insertAllotOPSLogSQL = "INSERT INTO impact2_ops_log (ops_hrms_code, ops_tab_name, ops_type, log_date,"
			+ " user_ip, to_user_hrms_code) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?)";

	String insertAddlPostSQL = "INSERT INTO impact2_additional_posting(hrms_code, circle_cd, charge_cd, office_cd, "
			+ " log_dt, approver_hrms) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";

	String fetchAssigned = "SELECT um.hrms_code AS hrmsCode, um.full_name AS fullName, up.approver_hrms, up.office_type,um.bo_id,(select designation from designation_cd where desig_cd=um.desig_cd),\r\n"
			+ "	(SELECT full_name FROM impact2_user_master WHERE hrms_code = up.approver_hrms) AS assignedBy, "
			+ "	 um.email AS email, um.phone_no AS phoneNo, up.charge_cd AS chargeCd, up.circle_cd AS circleCd, "
			+ "	 up.office_cd AS officeId, ARRAY_AGG(DISTINCT up.role_id) AS role,(select role_name from impact2_role_master where role_id=up.role_id) role_name,"
			+ "	 ARRAY_AGG(DISTINCT pm.project_id::text) AS projectIds, ARRAY_AGG(DISTINCT pd.project_name) AS projectNames, "
			+ "	 ARRAY_AGG(DISTINCT up.office_cd) AS officeCds, up.approver_hrms AS approverHrms, "
			+ "	 c.charge_nm AS charge_name, cc.circle_nm AS circle_name, oc.office_nm AS office_name, "
			+ "	 pi.image_url AS imageurl FROM impact2_user_master um "
			+ "	 LEFT JOIN impact2_user_posting up ON um.hrms_code = up.hrms_code "
			+ "	 LEFT JOIN impact2_user_project_mapping pm ON um.hrms_code = pm.hrms_code "
			+ "	 LEFT JOIN impact2_project_details pd ON pm.project_id = pd.project_id "
			+ "	 LEFT JOIN charge_cd c ON up.charge_cd = c.charge_cd "
			+ "	 LEFT JOIN circle_cd cc ON up.circle_cd = cc.circle_cd "
			+ "	 LEFT JOIN office_cd oc ON up.office_cd = oc.office_cd "
			+ "	 LEFT JOIN impact2_profile_image pi ON um.hrms_code = pi.hrms_code "
			+ "	 WHERE up.status = 'L' AND up.approver_hrms = ?"
			+ "	 GROUP BY um.hrms_code, um.full_name, up.approver_hrms, um.email, um.phone_no, up.charge_cd,"
			+ "	 up.circle_cd, up.office_cd, c.charge_nm, cc.circle_nm, oc.office_nm, up.office_type, pi.image_url,role_id";

	String insertRoleMasterSQL = "INSERT INTO impact2_user_role_master (hrms_id, role_id) VALUES (?, ?) "
			+ " ON CONFLICT (hrms_id, role_id) "
			+ " DO UPDATE SET role_id = EXCLUDED.role_id";

	String INSERT_USER_POSTING_DET = "insert into impact2_user_posting_det (hrms_code,posting_type,office_type,office_cd,active_dt,"
			+ " inactive_dt,status,approver_hrms_code,role_id,log_dt) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) "
			+ " ON CONFLICT (hrms_code, posting_type, office_type, office_cd) " + " DO UPDATE SET "
			+ " active_dt = EXCLUDED.active_dt," + " inactive_dt = EXCLUDED.inactive_dt," + " status = EXCLUDED.status,"
			+ " approver_hrms_code = EXCLUDED.approver_hrms_code," + " role_id = EXCLUDED.role_id, "
			+ " log_dt = CURRENT_TIMESTAMP ";

	String INSERT_USER_PROJECT_DET = "insert into impact2_user_project_det (hrms_code,project_id,role_id,status,active_dt, "
			+ " inactive_dt,log_dt) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) "
			+ " ON CONFLICT (hrms_code, project_id) " + " DO UPDATE SET " + " role_id = EXCLUDED.role_id, "
			+ " status = EXCLUDED.status, " + " active_dt = EXCLUDED.active_dt, "
			+ " inactive_dt = EXCLUDED.inactive_dt, " + " log_dt = CURRENT_TIMESTAMP ";
	
	String UPDATE_USER_POSTING_DET="update impact2_user_posting_det set status=?,inactive_dt=? where hrms_code=? "
			+ " and posting_type=? and office_type=? and office_cd=? ";
	
	String UPDATE_USER_PROJECT_DET=" update impact2_user_project_det set status=?,inactive_dt=? where hrms_code=? and "
			+ " project_id=? and role_id=? ";
	
	String UPDATE_USER_STATUS="update impact2_user_master set usr_status_cd=? where hrms_code=?";

	// User posting details fetch

	String SELECT_USER_POSTING_DET = "SELECT upd.hrms_code,upd.posting_type,upd.office_type,upd.office_cd AS office_id,"
			+ "  CASE " + "  WHEN upd.office_type = 'CI' THEN cim.circle_nm "
			+ "  WHEN upd.office_type = 'CH' THEN chm.charge_nm " + "  WHEN upd.office_type = 'OF' THEN ofm.office_nm "
			+ "  ELSE NULL " + "  END AS office_name, "
			+ "  TO_CHAR(upd.active_dt, 'YYYY-MM-DD HH24:MI:SS') AS active_dt," + "  emp.full_name AS approver_name, "
			+ "  upd.status " + "  FROM impact2_user_posting_det upd "
			+ "  LEFT JOIN circle_cd cim ON upd.office_cd = cim.circle_cd AND upd.office_type = 'CI' "
			+ "  LEFT JOIN charge_cd chm ON upd.office_cd = chm.charge_cd AND upd.office_type = 'CH' "
			+ "  LEFT JOIN office_cd ofm ON upd.office_cd = ofm.office_cd AND upd.office_type = 'OF' "
			+ "  LEFT JOIN impact2_user_master emp ON upd.approver_hrms_code = emp.hrms_code "
			+ "  WHERE upd.hrms_code = ? and upd.status=?";

	// user project details fetch

	String SELECT_USER_PROJECT_DET = "SELECT  upd.project_id, pm.project_name,upd.role_id,rm.role_name "
			+ " FROM impact2_user_project_det upd "
			+ " LEFT JOIN impact2_project_details pm ON upd.project_id = pm.project_id "
			+ " LEFT JOIN impact2_role_master rm ON upd.role_id = rm.role_id " + " WHERE upd.hrms_code = ?  and upd.status=?";

	//  Fetch all users with postings (join posting and user master)
	String SQL_FETCH_USERS_FROM_POSTINGS = "SELECT DISTINCT um.hrms_code,um.full_name,um.email,d.designation AS "
			+ " desig_name,um.bo_id,upi.image_url AS profile_image_url FROM impact2_user_posting_det upd "
			+ " INNER JOIN impact2_user_master um ON upd.hrms_code = um.hrms_code LEFT JOIN designation_cd d "
			+ " ON um.desig_cd = d.desig_cd LEFT JOIN impact2_profile_image upi ON um.hrms_code = upi.hrms_code"
			+ " WHERE upd.status = 'L' ORDER BY um.hrms_code";
	
	String SQL_FETCH_USERS_BASE_POSTINGS ="SELECT DISTINCT  um.hrms_code, um.full_name, um.email, d.designation AS "
			+ " desig_name, um.bo_id,upi.image_url AS profile_image_url FROM impact2_user_posting_det upd INNER JOIN "
			+ " impact2_user_master um ON upd.hrms_code = um.hrms_code LEFT JOIN designation_cd d ON "
			+ " um.desig_cd = d.desig_cd LEFT JOIN impact2_profile_image upi ON um.hrms_code = upi.hrms_code "
			+ " WHERE upd.status = 'L'  AND upd.office_cd IN (:officeIds) ORDER BY um.hrms_code";

	//  Fetch all postings (can filter by office type/code if needed)
	String SQL_FETCH_POSTINGS = "SELECT upd.hrms_code,upd.posting_type,upd.office_type,upd.office_cd AS office_id,"
			+ "  CASE " + "  WHEN upd.office_type = 'CI' THEN cim.circle_nm "
			+ "  WHEN upd.office_type = 'CH' THEN chm.charge_nm " + "  WHEN upd.office_type = 'OF' THEN ofm.office_nm "
			+ "  ELSE NULL " + "  END AS office_name, "
			+ "  TO_CHAR(upd.active_dt, 'YYYY-MM-DD HH24:MI:SS') AS active_dt," + "  emp.full_name AS approver_name, "
			+ "  upd.status " + "  FROM impact2_user_posting_det upd "
			+ "  LEFT JOIN circle_cd cim ON upd.office_cd = cim.circle_cd AND upd.office_type = 'CI' "
			+ "  LEFT JOIN charge_cd chm ON upd.office_cd = chm.charge_cd AND upd.office_type = 'CH' "
			+ "  LEFT JOIN office_cd ofm ON upd.office_cd = ofm.office_cd AND upd.office_type = 'OF' "
			+ "  LEFT JOIN impact2_user_master emp ON upd.approver_hrms_code = emp.hrms_code "
			+ "  WHERE upd.status = ? ";

	// Fetch all projects for these users

	String SQL_FETCH_PROJECTS = "SELECT upd.hrms_code, upd.project_id, pm.project_name, upd.role_id, rm.role_name, "
			+ " upd.status, TO_CHAR(upd.active_dt, 'YYYY-MM-DD HH24:MI:SS') AS active_dt, "
			+ " TO_CHAR(upd.inactive_dt, 'YYYY-MM-DD HH24:MI:SS') AS inactive_dt "
			+ " FROM impact2_user_project_det upd "
			+ " LEFT JOIN impact2_project_details pm ON upd.project_id = pm.project_id "
			+ " LEFT JOIN impact2_role_master rm ON upd.role_id = rm.role_id " + "WHERE upd.status = ?";

	public boolean saveAllotData(AssignRequest allotbean, String assignHrmsCd) throws Exception;

	public boolean saveAdditionalPosting(UserAssignRequest allotbean, String assignHrmsCd);

	public boolean saveAllotLog(AssignRequest allotbean, String ops_tab_name, String ops_type, String assignHrmsCd);

	public boolean saveRoleForAssign(String hrmsCode,String roleId);

	public boolean saveAllotProject(AssignRequest allotbean);

	public List<AssignRequest> fetchAssignData(String assignHrmsCd) throws Exception;

	public List<UserAssignPostingDet> fetchUserPostingDet(String hrmsCd);

	public List<UserAssignProjectDet> fetchUserProjectDet(String hrmsCd);

	public boolean saveUserPostingData(UserAssignRequest userAssignReq, String assignHrmsCd) throws Exception;

	public boolean saveUserModulesData(UserAssignRequest userAssignReq, String assignHrmsCd) throws Exception;

	public List<UserAssignDet> getAllUsersWithPostingsAndProjects(String hrmsCd,String role);

	public boolean releaseUserPostingData(UserReleaseRequest userRelReq, String releaseHrmsCd) throws Exception;

	public boolean releaseUserModulesData(UserReleaseRequest userRelReq, String releaseHrmsCd) throws Exception;
	
	public boolean updateUserStatus(String status,String hrmsCd) throws Exception;
	
	public int fetchUserPostingCount(String hrmsCd) throws Exception;
	
	public List<UserAssignDet> getAllUsersWithPostings(String hrmsCd, String role);

}
