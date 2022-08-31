package example.springbootdynamodb.exception;

public class InternalErrorException extends RuntimeException {

  public InternalErrorException(String message) {
    super(message);
  }

  public InternalErrorException(String message, Throwable cause) {
    super(message, cause);
  }

  public InternalErrorException(String message, String... formatArgs) {
    super(String.format(message, formatArgs));
  }

  public InternalErrorException(Throwable cause, String message, String... formatArgs) {
    super(String.format(message, formatArgs), cause);
  }
}
