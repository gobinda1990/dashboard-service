package comtax.gov.webapp.repo;

import comtax.gov.webapp.model.AssignRequest;

public interface UserRepository {
	
	String insertAllotSQL = "INSERT INTO impact2_user_posting (hrms_code,posting_type,office_type,office_cd,active_dt,status,approver_hrms,log_dt,role_id,charge_cd,circle_cd)"
			+ " VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, 'A', ?,  CURRENT_TIMESTAMP,?,?,?)";
	
	
	String insertAllotProjectSQL = "INSERT INTO impact2_user_project_mapping "
	        + "(hrms_code, project_id, status, log_dt) "
	        + "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
	
	String insertAllotOPSLogSQL = "INSERT INTO operation_logs "
	        + "(ops_hrms_code, ops_tab_name, ops_type, log_date, user_ip, to_user_hrms_code) "
	        + "VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, ?)";
	
	public boolean saveAssignedUserData(AssignRequest assignRequest);

}
