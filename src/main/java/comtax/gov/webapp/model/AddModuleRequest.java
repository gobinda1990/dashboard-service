package comtax.gov.webapp.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddModuleRequest {
	
	 @NotBlank(message = "Module name is required")
	    private String moduleName;

	    @NotBlank(message = "Module URL is required")
	    private String moduleUrl;

}
