package comtax.gov.webapp.repo;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import comtax.gov.webapp.model.AssignRequest;

public class UserPostingRowMapper implements RowMapper<AssignRequest> {

    
	public AssignRequest mapRow(ResultSet rs, int rowNum) throws SQLException {

	    AssignRequest bean = new AssignRequest();

	    bean.setHrmsCode(rs.getString("hrmsCode"));
	    bean.setFullName(rs.getString("fullName"));
	    bean.setAssignedBy(rs.getString("assignedBy"));
	    bean.setEmail(rs.getString("email"));
	    bean.setPhoneNo(rs.getString("phoneNo"));

	    bean.setChargeCd(rs.getString("chargeCd"));
	    bean.setCircleCd(rs.getString("circleCd"));
	    bean.setOfficeId(rs.getString("officeId"));

	    bean.setRoleId(getList(rs, "role"));
	    bean.setProjectIds(getList(rs, "projectIds"));
	    bean.setOfficeCds(getList(rs, "officeCds"));
	    bean.setProjectNames(getList(rs, "projectNames"));

	    bean.setApproverHrms(rs.getString("assignedBy")); // same column
	    bean.setUserIp(null); // column not in query

	    bean.setCharge_name(rs.getString("charge_name"));
	    bean.setCircle_name(rs.getString("circle_name"));
	    bean.setOffice_name(rs.getString("office_name"));
	    
	    bean.setOffice_type(rs.getString("office_type"));
	    bean.setImageurl(rs.getString("imageurl"));
	    
	    bean.setDesignation(rs.getString("designation"));
	    bean.setBo_id(rs.getString("bo_id"));
	    bean.setRole_name(rs.getString("role_name"));

	    // MAIN POSTING LOGIC
	    if (bean.getCharge_name() != null) {
	        bean.setMain_posting(bean.getCharge_name());
	    } else if (bean.getCircle_name() != null) {
	        bean.setMain_posting(bean.getCircle_name());
	    } else if (bean.getOffice_name() != null) {
	        bean.setMain_posting(bean.getOffice_name());
	    } else {
	        bean.setMain_posting(null);
	    }

	    return bean;
	}

    private List<String> getList(ResultSet rs, String column) throws SQLException {
        Array array = rs.getArray(column);
        if (array == null) return null;
        return Arrays.asList((String[]) array.getArray());
    }

}
