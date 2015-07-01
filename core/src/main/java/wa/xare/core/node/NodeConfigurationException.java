package wa.xare.core.node;

public class NodeConfigurationException extends RuntimeException {

  public NodeConfigurationException() {
    super("Could not configure node.");
  }

  public NodeConfigurationException(String nodeName, String missingField) {
    super("Node " + nodeName + " requires defining the field " + missingField);
  }

  public NodeConfigurationException(String msg) {
    super(msg);
  }

  public NodeConfigurationException(String msg, Throwable cause) {
    super(msg, cause);
  }

}
