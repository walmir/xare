package wa.xare.core.node.endpoint;

@SuppressWarnings("serial")
public class EndpointConfigurationException extends RuntimeException {

	public EndpointConfigurationException(Throwable cause) {
		super("could not configure endpoint", cause);
	}

	public EndpointConfigurationException(String msg) {
		super(msg);
	}

}
