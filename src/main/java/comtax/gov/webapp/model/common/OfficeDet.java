package comtax.gov.webapp.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter
public class OfficeDet implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String officeCd;
	private String officeNm;

}
