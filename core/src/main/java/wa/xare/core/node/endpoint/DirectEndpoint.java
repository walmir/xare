package wa.xare.core.node.endpoint;

import org.vertx.java.core.logging.Logger;

import wa.xare.core.Route;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.PacketBuilder;

public class DirectEndpoint extends AbstractEndpoint {

	private String address;
	private Logger logger;

  public DirectEndpoint(Route route,
      EndpointDirection direction, String address) {
    super(route, direction);
		this.address = address;
    logger = route.getContainer().logger();
	}

	@Override
	protected void deliverOutgoingMessage(Object message) {
		if (direction == EndpointDirection.OUTGOING) {
			route.getContainer().logger().debug(message);
			route.getVertx().eventBus().send(address, message);
		}
	}

	@Override
	protected void deployAsOutgoingEndpoint() {
		// Nothing to do.
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Sets up the endpoint to listen for incoming messages on the defined
	 * {@link #address}.
	 * </p>
	 */
	@Override
	protected void deployAsIncomingEndpoint() {
		if (direction == EndpointDirection.INCOMING) {
			route.getVertx().eventBus().registerHandler(address, message -> {
				logger.debug("recieved packet: " + message.body());
				Packet packet = PacketBuilder.build(message);
				notifyHandler(packet);
			});
		}

	}

}
