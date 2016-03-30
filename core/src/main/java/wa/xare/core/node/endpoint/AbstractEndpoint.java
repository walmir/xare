package wa.xare.core.node.endpoint;

import wa.xare.core.annotation.Field;
import wa.xare.core.node.AbstractNode;
import wa.xare.core.packet.Packet;

public abstract class AbstractEndpoint extends AbstractNode implements
    Endpoint {

  @Field(required = false)
	protected EndpointDirection direction;

  @Field
  protected String address;

	private EndpointHandler handler;

  protected AbstractEndpoint(EndpointDirection direction) {
		this.direction = direction;
	}

  public AbstractEndpoint() {
  }

  public EndpointDirection getDirection() {
    return direction;
  }

  public void setDirection(EndpointDirection direction) {
    this.direction = direction;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
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

}
