package comtax.gov.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignProjectDet {
	
	private String projectId;
	private String projectName;
	private String roleId;
	private String roleName;
	private String status;
    private String activeDt;
    private String inactiveDt;

}
