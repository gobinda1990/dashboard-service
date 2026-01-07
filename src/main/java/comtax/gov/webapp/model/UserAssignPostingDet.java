package comtax.gov.webapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignPostingDet {
	
	private String hrmsCode;
	private String postingType;
	private String officeType;
	private String officeId;
	private String officeName;;
	private String activeDt;
	private String approverName;
	private String status;

}
