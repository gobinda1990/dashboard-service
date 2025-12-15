package comtax.gov.webapp.model;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupBean {

    @NotBlank(message = "HRMS code is required")
    private String hrms_code;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String passwd;

    @NotBlank(message = "Full name is required")
    private String full_name;

//    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone_no;

    private String usr_status_cd;
    private String usr_level_cd;
    private String desig_cd;
    private String gpf_no;
    private String circle_cd;
    private String charge_cd;
    private String usr_pwd_creation_dt;
    private String password_expire_dt;
    private String hint_qs_cd;
    private String hint_ans;
    private String gender;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String dt_of_birth;
    private String office_cd;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dt_of_join;

    @Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]{1}$", message = "Invalid PAN format")
    private String pan_no;

    private String bo_id;
}
