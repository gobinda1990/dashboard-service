package comtax.gov.webapp.repo;

import java.util.List;

import comtax.gov.webapp.model.AllotEmployeeBean;
import comtax.gov.webapp.model.AssignRequest;
import comtax.gov.webapp.model.AssignedEmpBean;

public interface AssignEmpRepo {
	
	
	String insertAllotSQL = "INSERT INTO impact2_user_posting (hrms_code,posting_type,office_type,office_cd,active_dt,status,approver_hrms,log_dt,role_id,charge_cd,circle_cd)"
			+ " VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'L', ?,  CURRENT_TIMESTAMP,?,?,?)";
	
	
	String insertAllotProjectSQL = "INSERT INTO impact2_user_project_mapping "
	        + "(hrms_code, project_id, status, log_dt) "
	        + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
	
	String insertAllotOPSLogSQL = "INSERT INTO impact2_ops_log "
	        + "(ops_hrms_code, ops_tab_name, ops_type, log_date, user_ip, to_user_hrms_code) "
	        + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?)";
	
	String insertAddlPostSQL = "INSERT INTO impact2_additional_posting(\r\n"
			+ "	hrms_code, circle_cd, charge_cd, office_cd, log_dt, approver_hrms)\r\n"
			+ "	VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?)";
	
//	String fetchAssigned="SELECT \r\n"
//			+ "    um.hrms_code AS hrmsCode,\r\n"
//			+ "    um.full_name AS fullName,\r\n"
//			+ "    up.approver_hrms AS assignedBy,\r\n"
//			+ "    um.email AS email,\r\n"
//			+ "    um.phone_no AS phoneNo,\r\n"
//			+ "\r\n"
//			+ "    up.charge_cd AS chargeCd,\r\n"
//			+ "    up.circle_cd AS circleCd,\r\n"
//			+ "    up.office_cd AS officeId,\r\n"
//			+ "\r\n"
//			+ "    ARRAY_AGG(DISTINCT up.role_id) AS role,\r\n"
//			+ "    ARRAY_AGG(DISTINCT pm.project_id::text) AS projectIds,\r\n"
//			+ "    ARRAY_AGG(DISTINCT pd.project_name) AS projectNames,\r\n"
//			+ "    ARRAY_AGG(DISTINCT up.office_cd) AS officeCds,\r\n"
//			+ "\r\n"
//			+ "    up.approver_hrms AS approverHrms,\r\n"
//			+ "\r\n"
//			+ "    c.charge_nm AS charge_name,\r\n"
//			+ "    cc.circle_nm AS circle_name,\r\n"
//			+ "    oc.office_nm AS office_name\r\n"
//			+ "\r\n"
//			+ "FROM impact2_user_master um\r\n"
//			+ "LEFT JOIN impact2_user_posting up \r\n"
//			+ "    ON um.hrms_code = up.hrms_code\r\n"
//			+ "\r\n"
//			+ "LEFT JOIN impact2_user_project_mapping pm \r\n"
//			+ "    ON um.hrms_code = pm.hrms_code\r\n"
//			+ "\r\n"
//			+ "LEFT JOIN impact2_project_details pd \r\n"
//			+ "    ON pm.project_id = pd.project_id\r\n"
//			+ "\r\n"
//			+ "LEFT JOIN charge_cd c \r\n"
//			+ "    ON up.charge_cd = c.charge_cd\r\n"
//			+ "\r\n"
//			+ "LEFT JOIN circle_cd cc \r\n"
//			+ "    ON up.circle_cd = cc.circle_cd\r\n"
//			+ "\r\n"
//			+ "LEFT JOIN office_cd oc \r\n"
//			+ "    ON up.office_cd = oc.office_cd\r\n"
//			+ "\r\n"
////			+ "WHERE up.approver_hrms = '2020'\r\n"
//+ "WHERE up.status = 'L'\r\n"
//			+ "\r\n"
//			+ "GROUP BY \r\n"
//			+ "    um.hrms_code, um.full_name, up.approver_hrms,\r\n"
//			+ "    um.email, um.phone_no,\r\n"
//			+ "    up.charge_cd, up.circle_cd, up.office_cd,\r\n"
//			+ "    c.charge_nm, cc.circle_nm, oc.office_nm\r\n";
			
			
//	String fetchAssigned = "SELECT um.hrms_code AS hrmsCode, um.full_name AS fullName, up.approver_hrms,(select full_name from impact2_user_master where hrms_code=up.approver_hrms) AS assignedBy, um.email AS email, um.phone_no AS phoneNo, up.charge_cd AS chargeCd, up.circle_cd AS circleCd, up.office_cd AS officeId, ARRAY_AGG(DISTINCT up.role_id) AS role, ARRAY_AGG(DISTINCT pm.project_id::text) AS projectIds, ARRAY_AGG(DISTINCT pd.project_name) AS projectNames, ARRAY_AGG(DISTINCT up.office_cd) AS officeCds, up.approver_hrms AS approverHrms, c.charge_nm AS charge_name, cc.circle_nm AS circle_name, oc.office_nm AS office_name FROM impact2_user_master um LEFT JOIN impact2_user_posting up ON um.hrms_code = up.hrms_code LEFT JOIN impact2_user_project_mapping pm ON um.hrms_code = pm.hrms_code LEFT JOIN impact2_project_details pd ON pm.project_id = pd.project_id LEFT JOIN charge_cd c ON up.charge_cd = c.charge_cd LEFT JOIN circle_cd cc ON up.circle_cd = cc.circle_cd LEFT JOIN office_cd oc ON up.office_cd = oc.office_cd WHERE up.status = 'L' GROUP BY um.hrms_code, um.full_name, up.approver_hrms, um.email, um.phone_no, up.charge_cd, up.circle_cd, up.office_cd, c.charge_nm, cc.circle_nm, oc.office_nm";
//	String fetchAssigned ="SELECT um.hrms_code AS hrmsCode, um.full_name AS fullName, up.approver_hrms, up.office_type,\r\n"
//			+ "       (SELECT full_name FROM impact2_user_master WHERE hrms_code = up.approver_hrms) AS assignedBy,\r\n"
//			+ "       um.email AS email, um.phone_no AS phoneNo, up.charge_cd AS chargeCd, up.circle_cd AS circleCd,\r\n"
//			+ "       up.office_cd AS officeId, ARRAY_AGG(DISTINCT up.role_id) AS role,\r\n"
//			+ "       ARRAY_AGG(DISTINCT pm.project_id::text) AS projectIds, ARRAY_AGG(DISTINCT pd.project_name) AS projectNames,\r\n"
//			+ "       ARRAY_AGG(DISTINCT up.office_cd) AS officeCds, up.approver_hrms AS approverHrms,\r\n"
//			+ "       c.charge_nm AS charge_name, cc.circle_nm AS circle_name, oc.office_nm AS office_name,\r\n"
//			+ "       pi.image_url AS imageurl\r\n"
//			+ "FROM impact2_user_master um\r\n"
//			+ "LEFT JOIN impact2_user_posting up ON um.hrms_code = up.hrms_code\r\n"
//			+ "LEFT JOIN impact2_user_project_mapping pm ON um.hrms_code = pm.hrms_code\r\n"
//			+ "LEFT JOIN impact2_project_details pd ON pm.project_id = pd.project_id\r\n"
//			+ "LEFT JOIN charge_cd c ON up.charge_cd = c.charge_cd\r\n"
//			+ "LEFT JOIN circle_cd cc ON up.circle_cd = cc.circle_cd\r\n"
//			+ "LEFT JOIN office_cd oc ON up.office_cd = oc.office_cd\r\n"
//			+ "LEFT JOIN impact2_profile_image pi ON um.hrms_code = pi.hrms_code\r\n"
//			+ "WHERE up.status = 'L' AND up.approver_hrms = ?\r\n"
//			+ "GROUP BY um.hrms_code, um.full_name, up.approver_hrms, um.email, um.phone_no, up.charge_cd,\r\n"
//			+ "         up.circle_cd, up.office_cd, c.charge_nm, cc.circle_nm, oc.office_nm, up.office_type, pi.image_url";
String fetchAssigned="SELECT um.hrms_code AS hrmsCode, um.full_name AS fullName, up.approver_hrms, up.office_type,um.bo_id,(select designation from designation_cd where desig_cd=um.desig_cd),\r\n"
		+ "			       (SELECT full_name FROM impact2_user_master WHERE hrms_code = up.approver_hrms) AS assignedBy,\r\n"
		+ "			       um.email AS email, um.phone_no AS phoneNo, up.charge_cd AS chargeCd, up.circle_cd AS circleCd,\r\n"
		+ "			       up.office_cd AS officeId, ARRAY_AGG(DISTINCT up.role_id) AS role,(select role_name from impact2_role_master where role_id=up.role_id) role_name,\r\n"
		+ "			       ARRAY_AGG(DISTINCT pm.project_id::text) AS projectIds, ARRAY_AGG(DISTINCT pd.project_name) AS projectNames,\r\n"
		+ "			       ARRAY_AGG(DISTINCT up.office_cd) AS officeCds, up.approver_hrms AS approverHrms,\r\n"
		+ "			       c.charge_nm AS charge_name, cc.circle_nm AS circle_name, oc.office_nm AS office_name,\r\n"
		+ "			       pi.image_url AS imageurl\r\n"
		+ "			FROM impact2_user_master um\r\n"
		+ "			LEFT JOIN impact2_user_posting up ON um.hrms_code = up.hrms_code\r\n"
		+ "			LEFT JOIN impact2_user_project_mapping pm ON um.hrms_code = pm.hrms_code\r\n"
		+ "			LEFT JOIN impact2_project_details pd ON pm.project_id = pd.project_id\r\n"
		+ "			LEFT JOIN charge_cd c ON up.charge_cd = c.charge_cd\r\n"
		+ "			LEFT JOIN circle_cd cc ON up.circle_cd = cc.circle_cd\r\n"
		+ "			LEFT JOIN office_cd oc ON up.office_cd = oc.office_cd\r\n"
		+ "			LEFT JOIN impact2_profile_image pi ON um.hrms_code = pi.hrms_code\r\n"
		+ "			WHERE up.status = 'L' AND up.approver_hrms = ?\r\n"
		+ "			GROUP BY um.hrms_code, um.full_name, up.approver_hrms, um.email, um.phone_no, up.charge_cd,\r\n"
		+ "			         up.circle_cd, up.office_cd, c.charge_nm, cc.circle_nm, oc.office_nm, up.office_type, pi.image_url,role_id";
	String insertRoleMasterSQL = "insert into impact2_user_role_master values(?,?)";
	
	public boolean saveAllotData(AssignRequest allotbean)throws Exception;
	
	public List<AssignRequest> fetchAssignData()throws Exception;
//	public boolean saveAllotProject(AllotEmployeeBean allotbean);
//	public boolean saveAllotLog(AllotEmployeeBean allotbean);

}
