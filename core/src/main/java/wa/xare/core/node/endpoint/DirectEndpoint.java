package wa.xare.core.node.endpoint;


import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import wa.xare.core.annotation.EndpointType;
import wa.xare.core.configuration.NodeConfiguration;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.PacketBuilder;

@EndpointType
public class DirectEndpoint extends AbstractEndpoint {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(DirectEndpoint.class);


  public DirectEndpoint() {
    super();
  }

  public DirectEndpoint(EndpointDirection direction, String address) {
    super(direction);
		this.address = address;
	}

	@Override
	protected void deliverOutgoingMessage(Object message) {
		if (direction == EndpointDirection.OUTGOING) {
      LOGGER.debug(message);

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
      route.getVertx().eventBus().consumer(address, message -> {
        LOGGER.debug("recieved packet: " + message.body());
				Packet packet = PacketBuilder.build(message);
				notifyHandler(packet);
			});
		}
  }

}
