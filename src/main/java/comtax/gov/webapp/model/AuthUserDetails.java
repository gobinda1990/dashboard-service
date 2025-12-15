package comtax.gov.webapp.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class AuthUserDetails {
	
	private String hrmsCd;
	private String circleCd;
	private String chargeCd;
	private String emailId;
	private String phoneNo;	
    private String gpfNo;
    private String panNo;
    private String boId;
	private List<String> role;
	private boolean admin;

}
