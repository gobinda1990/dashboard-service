package comtax.gov.webapp.model.common;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserDet implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String hrmsCode;   
    private String fullName;
    private String email;
    private String phoneNo;    
    private String desigName;
    private String gpfNo;   
    private String panNo;
    private String boId;
    private String gender;
    private String profileImageUrl;
    private List<String> role;    
    private String postingType;    
    private List<String> officeTypes;
    private List<String> circleCds;
    private List<String> chargeCds;
    private List<String> officeCds;   
  

}
