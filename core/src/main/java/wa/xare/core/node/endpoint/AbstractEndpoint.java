package wa.xare.core.node.endpoint;

import wa.xare.core.Route;
import wa.xare.core.node.DefaultRouteNode;
import wa.xare.core.packet.Packet;

public abstract class AbstractEndpoint extends DefaultRouteNode implements
    Endpoint {

	protected EndpointDirection direction;

  protected Route route;
	private EndpointHandler handler;

  protected AbstractEndpoint(Route route, EndpointDirection direction) {
    this.route = route;
		this.direction = direction;
	}

	// protected Route getRoute() {
	// return route;
	// }

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
