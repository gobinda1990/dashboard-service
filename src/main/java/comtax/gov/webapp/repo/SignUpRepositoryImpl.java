package comtax.gov.webapp.repo;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import comtax.gov.webapp.exception.DataSaveException;
import comtax.gov.webapp.exception.DatabaseOperationException;
import comtax.gov.webapp.exception.ServiceException;
import comtax.gov.webapp.model.SignupBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Repository responsible for persisting and retrieving signup data.
 * Handles all database-level operations for user registration.
 */
@Repository
@Slf4j
@RequiredArgsConstructor
public class SignUpRepositoryImpl implements SignUpRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // =====================================================
    // === Save Signup Data ================================
    // =====================================================

    /**
     * Inserts new user signup data into the database.
     *
     * @param signupBean user registration data
     * @return true if inserted successfully
     */
    @Override
    public boolean saveSignupData(SignupBean signupBean) {
        log.info("Attempting to save signup data for HRMS Code: {}", signupBean.getHrms_code());

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("hrms_code", signupBean.getHrms_code());
            params.put("passwd", signupBean.getPasswd());
            params.put("full_name", signupBean.getFull_name());
            params.put("email", signupBean.getEmail());
            params.put("phone_no", signupBean.getPhone_no());
            params.put("usr_status_cd", "A");
            params.put("usr_level_cd", signupBean.getUsr_level_cd());
            params.put("desig_cd", signupBean.getDesig_cd());
            params.put("gpf_no", signupBean.getGpf_no());
            params.put("circle_cd", signupBean.getCircle_cd());
            params.put("charge_cd", signupBean.getCharge_cd());
            params.put("dt_of_join", Timestamp.valueOf(signupBean.getDt_of_join().atStartOfDay()));
            params.put("pan_no", signupBean.getPan_no());
            params.put("bo_id", signupBean.getBo_id());
            params.put("dt_of_birth", Timestamp.valueOf(signupBean.getDt_of_birth().atStartOfDay()));
            params.put("hint_qs_cd", signupBean.getHint_qs_cd());
            params.put("hint_ans", signupBean.getHint_ans());
            params.put("office_cd", signupBean.getOffice_cd());
            params.put("gender", signupBean.getGender());

            int rows = namedParameterJdbcTemplate.update(SIGN_UP_SQL, params);

            if (rows > 0) {
                log.info("Signup successful for HRMS Code: {}", signupBean.getHrms_code());
                return true;
            } else {
                log.warn("No rows affected while inserting signup for HRMS Code: {}", signupBean.getHrms_code());
                throw new DataSaveException("Signup failed — no records inserted for HRMS " + signupBean.getHrms_code());
            }

        } catch (DataAccessException dae) {
            log.error("Database access error during signup for HRMS [{}]", signupBean.getHrms_code(), dae);
            throw new DataSaveException(
                    "Database write error occurred while saving signup for HRMS " + signupBean.getHrms_code(), dae);

        } catch (Exception ex) {
            log.error("Unexpected error while saving signup data for HRMS [{}]", signupBean.getHrms_code(), ex);
            throw new ServiceException(
                    "Unexpected error occurred while saving signup data for HRMS " + signupBean.getHrms_code(), ex);
        }
    }

    // =====================================================
    // === Check User Existence ============================
    // =====================================================

    /**
     * Checks whether a user already exists in the database.
     *
     * @param hrmsCode HRMS code to verify
     * @return true if user already exists
     */
    @Override
    public boolean userExists(String hrmsCode) {
        final String SQL = "SELECT COUNT(*) FROM impact2_user_master WHERE hrms_code = ?";
        log.info("Checking if user exists for HRMS Code: {}", hrmsCode);

        try {
            Integer count = jdbcTemplate.queryForObject(SQL, Integer.class, hrmsCode);
            boolean exists = count != null && count > 0;
            log.debug("User existence check for HRMS [{}]: {}", hrmsCode, exists);
            return exists;

        } catch (DataAccessException dae) {
            log.error("Database error while checking user existence for HRMS [{}]", hrmsCode, dae);
            throw new DatabaseOperationException(
                    "Database read error while verifying user existence for HRMS " + hrmsCode, dae);

        } catch (Exception ex) {
            log.error("Unexpected error while checking user existence for HRMS [{}]", hrmsCode, ex);
            throw new ServiceException(
                    "Unexpected error occurred while verifying user existence for HRMS " + hrmsCode, ex);
        }
    }
}
