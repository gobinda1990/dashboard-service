package comtax.gov.webapp.model.common;

import java.io.Serializable;

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
    private String desigCd;
    private String gpfNo;   
    private String panNo;
    private String boId;
    private String profileImageUrl;

}
