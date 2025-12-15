package comtax.gov.webapp.model;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AssetDet represents an asset record assigned to a custodian user.
 * It holds core asset attributes and metadata for tracking and reporting.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetDet implements Serializable {

    private static final long serialVersionUID = 1L;

    private int assetId;                // Unique asset identifier
    private String assetName;           // Asset name or title
    private String assetType;           // Type: VEHICLE, COMPUTER, FURNITURE, etc.
    private String assetCategory;       // Category or department asset belongs to
    private String description;         // Optional asset description
    private String serialNumber;        // Serial or registration number
    private String purchaseDate;        // Purchase or allotment date (ISO or yyyy-MM-dd)
    private String status;              // ACTIVE, IN_USE, TRANSFERRED, DISPOSED, etc.
    private String assignedCustodian;   // HRMS or user ID of custodian
    private String location;            // Office or circle where asset is located
    private String projectName;         // Project/Department name linked to asset
    private double cost;                // Original or current value
    private String lastAuditDate;       // Last verified audit date
}
