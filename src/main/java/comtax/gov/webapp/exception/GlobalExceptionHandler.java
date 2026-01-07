package comtax.gov.webapp.exception;

import comtax.gov.webapp.model.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.servlet.NoHandlerFoundException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Global centralized exception handler for all controllers. Converts exceptions
 * into unified ApiResponse JSON structures.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	// =====================================================
	// === Authentication & Authorization Exceptions =======
	// =====================================================

	@ExceptionHandler({ UsernameNotFoundException.class, BadCredentialsException.class })
	public ResponseEntity<ApiResponse<String>> handleAuthErrors(Exception ex, HttpServletRequest request) {
		log.warn("Authentication error: {}", ex.getMessage());
		return build(HttpStatus.UNAUTHORIZED, "Invalid username or password.", ErrorCode.UNAUTHORIZED, request);
	}

	@ExceptionHandler({ AuthorizationDeniedException.class, AccessDeniedException.class })
	public ResponseEntity<ApiResponse<String>> handleAccessDenied(Exception ex, HttpServletRequest request) {
		log.warn("Access denied: {}", ex.getMessage());
		return build(HttpStatus.FORBIDDEN, "Access denied: You do not have permission to access this resource.",
				ErrorCode.FORBIDDEN, request);
	}

	// =====================================================
	// === Validation & Input Errors =======================
	// =====================================================

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<String>> handleValidationErrors(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String details = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.joining(", "));
		log.warn("Validation error: {}", details);
		return build(HttpStatus.BAD_REQUEST, "Validation failed: " + details, ErrorCode.VALIDATION_ERROR, request);
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiResponse<String>> handleBindErrors(BindException ex, HttpServletRequest request) {
		String details = ex.getBindingResult().getFieldErrors().stream()
				.map(err -> err.getField() + ": " + err.getDefaultMessage()).collect(Collectors.joining(", "));
		log.warn("Bind error: {}", details);
		return build(HttpStatus.BAD_REQUEST, "Binding failed: " + details, ErrorCode.VALIDATION_ERROR, request);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<String>> handleJsonParse(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		log.warn("Malformed JSON request: {}", ex.getMessage());
		return build(HttpStatus.BAD_REQUEST, "Malformed JSON request. Please check your request body.",
				ErrorCode.JSON_PARSE_ERROR, request);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<String>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpServletRequest request) {
		String supported = ex.getSupportedHttpMethods() != null
				? ex.getSupportedHttpMethods().stream().map(HttpMethod::name).collect(Collectors.joining(", "))
				: "N/A";
		log.warn("HTTP method {} not supported. Supported: {}", ex.getMethod(), supported);
		return build(HttpStatus.METHOD_NOT_ALLOWED,
				"Request method '" + ex.getMethod() + "' not supported. Supported methods: " + supported,
				ErrorCode.MISSING_PARAMETER, request);
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleNoHandler(NoHandlerFoundException ex, HttpServletRequest request) {
		log.warn("No handler found for URL: {}", ex.getRequestURL());
		return build(HttpStatus.NOT_FOUND, "The requested endpoint does not exist. Please check the URL.",
				ErrorCode.RESOURCE_NOT_FOUND, request);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<String>> handleResourceNotFound(ResourceNotFoundException ex,
			HttpServletRequest request) {
		log.warn("Resource not found: {}", ex.getMessage());
		return build(HttpStatus.NOT_FOUND, ex.getMessage() != null ? ex.getMessage() : "Resource not found.",
				ErrorCode.RESOURCE_NOT_FOUND, request);
	}

	// =====================================================
	// === Custom Domain Exceptions ========================
	// =====================================================

	@ExceptionHandler(DatabaseOperationException.class)
	public ResponseEntity<ApiResponse<String>> handleDatabaseOperation(DatabaseOperationException ex,
			HttpServletRequest request) {
		log.error("Database operation error: {}", ex.getMessage(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR,
				"Database operation failed. Please contact system administrator.", ErrorCode.DB_CONNECTION_ERROR,
				request);
	}

	@ExceptionHandler(DataSaveException.class)
	public ResponseEntity<ApiResponse<String>> handleAssignmentSave(DataSaveException ex,
			HttpServletRequest request) {
		log.error("Assignment save failed: {}", ex.getMessage(), ex);
		return build(HttpStatus.BAD_REQUEST, "Unable to save assignment data. Please verify your input and try again.",
				ErrorCode.SAVE_OPERATION_FAILED, request);
	}

	// =====================================================
	// === Infrastructure / Service Errors ================
	// =====================================================

	@ExceptionHandler({ DataAccessException.class, SQLException.class, DatabaseException.class })
	public ResponseEntity<ApiResponse<String>> handleDatabaseError(Exception ex, HttpServletRequest request) {
		log.error("Database exception occurred: {}", ex.getMessage(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Database error occurred. Please try again later.",
				ErrorCode.DB_CONNECTION_ERROR, request);
	}

	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<ApiResponse<String>> handleServiceUnavailable(ServiceException ex,
			HttpServletRequest request) {
		log.error("Service unavailable: {}", ex.getMessage(), ex);
		return build(HttpStatus.SERVICE_UNAVAILABLE, "Service temporarily unavailable. Please try again later.",
				ErrorCode.SERVICE_UNAVAILABLE, request);
	}

	@ExceptionHandler(EncryptionException.class)
	public ResponseEntity<ApiResponse<String>> handleEncryption(EncryptionException ex, HttpServletRequest request) {
		log.error("Encryption error: {}", ex.getMessage(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while securing your data.",
				ErrorCode.UNEXPECTED_ERROR, request);
	}

	@ExceptionHandler(AsyncRequestTimeoutException.class)
	public ResponseEntity<ApiResponse<String>> handleAsyncTimeout(AsyncRequestTimeoutException ex,
			HttpServletRequest request) {
		log.error("Async request timeout: {}", ex.getMessage(), ex);
		return build(HttpStatus.SERVICE_UNAVAILABLE, "The request timed out. Please try again later.",
				ErrorCode.EXTERNAL_API_TIMEOUT, request);
	}

	// =====================================================
	// === Generic Fallback ===============================
	// =====================================================

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> handleGeneric(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception: {}", ex.getMessage(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.",
				ErrorCode.UNEXPECTED_ERROR, request);
	}

	// =====================================================
	// === Utility Method =================================
	// =====================================================

	private ResponseEntity<ApiResponse<String>> build(HttpStatus status, String message, ErrorCode code,
			HttpServletRequest request) {

		String requestId = (String) request.getAttribute("requestId"); // Optional trace ID from filter
		if (requestId == null) {
			requestId = UUID.randomUUID().toString(); // fallback if not provided
		}

		ApiResponse<String> response = ApiResponse.<String>builder().status(status.value()).message(message)
				.errorCode(code).success(status.is2xxSuccessful()).timestamp(LocalDateTime.now())
				.path(request.getRequestURI()).requestId(requestId).build();

		return ResponseEntity.status(status).body(response);
	}
}
