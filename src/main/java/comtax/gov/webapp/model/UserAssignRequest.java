package comtax.gov.webapp.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignRequest {

	private String hrmsCode;
	private String postingType;
	private List<String> postingIds;
	private List<ModuleRow> modules;
	private List<PostingDet> postings;

}
