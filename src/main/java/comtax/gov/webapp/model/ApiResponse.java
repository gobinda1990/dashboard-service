package comtax.gov.webapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import comtax.gov.webapp.exception.ErrorCode;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Standardized API response wrapper for both success and error responses.
 *
 * @param <T> The type of response data
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** HTTP Status code (e.g., 200, 400, 500) */
    private int status;

    /** Short human-readable message */
    private String message;

    /** Application-specific error code */
    private ErrorCode errorCode;

    /** Optional data payload */
    private T data;

    /** Indicates if the response is a success */
    private boolean success;

    /** Unique ID for request tracing */
    private String requestId;

    /** The API path or endpoint that generated this response */
    private String path;

    /** Timestamp for when this response was generated */
    private LocalDateTime timestamp = LocalDateTime.now();

    // ============================
    // ==== Static Factory Methods
    // ============================

    /** Success with data */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /** Success without data */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /** Error with code and message */
    public static <T> ApiResponse<T> error(int status, String message, ErrorCode code) {
        return ApiResponse.<T>builder()
                .status(status)
                .message(message)
                .errorCode(code)
                .success(false)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /** Error using default message from ErrorCode */
    public static <T> ApiResponse<T> error(int status, ErrorCode code) {
        return error(status, code.getDefaultMessage(), code);
    }
}
