package comtax.gov.webapp.model.common;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDet implements Serializable {

	private static final long serialVersionUID = 1L;
	private int projectId;
	private String projectName;
	private String projectUrl;
}
