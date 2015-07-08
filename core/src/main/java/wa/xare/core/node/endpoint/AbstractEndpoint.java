package wa.xare.core.node.endpoint;

import wa.xare.core.api.Endpoint;
import wa.xare.core.api.EndpointDirection;
import wa.xare.core.api.Packet;
import wa.xare.core.api.Route;
import wa.xare.core.api.configuration.EndpointConfiguration;
import wa.xare.core.api.configuration.NodeConfiguration;
import wa.xare.core.node.AbstractNode;

public abstract class AbstractEndpoint extends AbstractNode implements
    Endpoint {

	protected EndpointDirection direction;
  protected String address;

	private EndpointHandler handler;


  protected AbstractEndpoint(EndpointDirection direction) {
		this.direction = direction;
	}

  public AbstractEndpoint() {
  }

  @Override
	public final void deploy() {
		if (direction == null) {
			throw new EndpointConfigurationException("endpoint direction not defined");
		}

		switch (direction) {
		case INCOMING:
			deployAsIncomingEndpoint();
			break;

		case OUTGOING:
			deployAsOutgoingEndpoint();
			break;

		}

	}

	protected abstract void deliverOutgoingMessage(Object object);

	/**
	 * Sets up the Endpoint as an Outgoing Endpoint. Configuration parameters
	 * required by this method have to be provided to the constructor.
	 * 
	 * @see #deployAsIncomingEndpoint()
	 */
	protected abstract void deployAsOutgoingEndpoint();

	/**
	 * Sets up the Endpoint as an Incoming Endpoint. Configuration parameters
	 * required by this method have to be provided to the constructor.
	 * 
	 * @see #deployAsOutgoingEndpoint()
	 */
	protected abstract void deployAsIncomingEndpoint();

	protected void notifyHandler(Packet packet) {
		handler.handleIncomingPacket(packet);
	}

	@Override
	public void setHandler(EndpointHandler handler) {
		this.handler = handler;
	}

	@Override
	public void startProcessing(Packet packet) {
		if (direction == EndpointDirection.OUTGOING) {
			deliverOutgoingMessage(packet.getBody());
		}
		// else do nothing
	}

  @Override
  public void configure(Route route, NodeConfiguration configuration) {
    EndpointConfiguration config = new EndpointConfiguration(configuration);
    this.direction = config.getEndpointDirection();
    this.address = config.getEndpointAddress();

    super.configure(route, configuration);
  }

}
