package comtax.gov.webapp.model;


import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAssignmentDet implements Serializable {
    private static final long serialVersionUID = 1L;
    private String hrmsCode;
    private String fullName;
    private String chargeName;
    private List<String> roles;
    private List<String> projectNames;
}

