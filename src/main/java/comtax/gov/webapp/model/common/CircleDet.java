package comtax.gov.webapp.model.common;

import java.io.Serializable;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class CircleDet implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String circleCd;
	private String circleNm;

}
