package comtax.gov.webapp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import comtax.gov.webapp.model.UserAssignPostingDet;

public class UserAssignPostingDetRowMapper implements RowMapper<UserAssignPostingDet> {

	@Override
	public UserAssignPostingDet mapRow(ResultSet rs, int rowNum) throws SQLException {
		UserAssignPostingDet det = new UserAssignPostingDet();
		det.setHrmsCode(rs.getString("hrms_code"));
		det.setPostingType(rs.getString("posting_type"));
		det.setOfficeType(rs.getString("office_type"));
		det.setOfficeId(rs.getString("office_id"));
		det.setOfficeName(rs.getString("office_name"));
		det.setActiveDt(rs.getString("active_dt"));
		det.setApproverName(rs.getString("approver_name"));
		det.setStatus(rs.getString("status"));
		return det;
	}

}
