package wa.xare.core.node.endpoint;

import wa.xare.core.DefaultRoute;
import wa.xare.core.node.NodeConfiguration;

public class DefaultHttpEndpoint extends AbstractEndpoint {

	protected DefaultHttpEndpoint(DefaultRoute defaultRoute, EndpointDirection direction) {
    super(direction);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void deliverOutgoingMessage(Object message) {
		// TODO Auto-generated method stub

	}

	@Override
  protected void doConfigure(NodeConfiguration configuration) {
    // TODO Auto-generated method stub

  }

  @Override
	protected void deployAsOutgoingEndpoint() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void deployAsIncomingEndpoint() {
		// TODO Auto-generated method stub

	}

}
