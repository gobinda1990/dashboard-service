package comtax.gov.webapp.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignDet {
	
	private String hrmsCode;
    private String fullName;
    private String email;
    private String desigName;
    private String boId;
    private String profileImageUrl;
    
    private List<UserAssignPostingDet> postings;   
    private List<UserAssignProjectDet> projects;

}
