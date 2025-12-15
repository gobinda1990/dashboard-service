package comtax.gov.webapp.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostingDet implements Serializable {

    private static final long serialVersionUID = 1L;

    private int postingId;       // Unique ID
    private String postingName;  // Office or location name
}

