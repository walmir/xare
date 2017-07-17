package wa.xare.core.exception;

public class InvalidRouteConfigurationException extends Exception {

  public static final String MISSING_REQUIRED_FIELD = "required field '%s' is not defined.";

  public InvalidRouteConfigurationException(String template, String... values) {
    super(String.format(template, new Object[]{values}));
  }

  public InvalidRouteConfigurationException(String msg, Throwable throwable) {
    super(msg, throwable);
  }

}
