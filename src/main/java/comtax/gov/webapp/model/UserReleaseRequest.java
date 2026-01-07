package comtax.gov.webapp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserReleaseRequest {
	
	private String hrmsCode;          
    private String remarks;          
    private List<ReleasePostingRequest> releasePostings;
    private List<ReleaseModuleRequest> releaseProjects;

}
