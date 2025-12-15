package comtax.gov.webapp.model;

import java.util.List;

import lombok.Data;
@Data
public class AssignedEmpBean {

	private String hrmsCode;
    private String fullName;
    private String assignedBy;
    private String email;
    private String phoneNo;

    private String chargeCd;
    private String circleCd;
    private String officeId;

    private List<String> role;
    private List<String> projectIds;       // from ARRAY_AGG(pm.project_id)
    private List<String> projectNames;     // from ARRAY_AGG(pd.project_name)
    private List<String> officeCds;

    private String approverHrms;
}
