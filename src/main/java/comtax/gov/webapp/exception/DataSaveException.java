package comtax.gov.webapp.exception;

public class DataSaveException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataSaveException(String message) {
		super(message);
	}

	public DataSaveException(String message, Throwable cause) {
		super(message, cause);
	}
}
