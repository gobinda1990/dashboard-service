package comtax.gov.webapp.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import comtax.gov.webapp.model.UserAssignProjectDet;

public class UserAssignProjectDetRowMapper implements RowMapper<UserAssignProjectDet> {

	@Override
	public UserAssignProjectDet mapRow(ResultSet rs, int rowNum) throws SQLException {

		UserAssignProjectDet det = new UserAssignProjectDet();
		det.setProjectId(rs.getString("project_id"));
		det.setProjectName(rs.getString("project_name"));
		det.setRoleId(rs.getString("role_id"));
		det.setRoleName(rs.getString("role_name"));
		return det;
	}

}
