package wa.xare.core.node.endpoint;

import org.vertx.java.core.logging.Logger;
import org.vertx.java.core.logging.impl.LoggerFactory;

import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.builder.EndpointType;
import wa.xare.core.packet.Packet;
import wa.xare.core.packet.PacketBuilder;

@EndpointType
public class DirectEndpoint extends AbstractEndpoint {
  // LoggerFactory.get
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
        LOGGER.debug("recieved packet: " + message.body());
				Packet packet = PacketBuilder.build(message);
				notifyHandler(packet);
			});
		}
  }

  @Override
  protected void doConfigure(NodeConfiguration configuration) {
    // Do nothing, config is done in super::configure()
    assert (this.direction != null) : "endpoint direction not set";
    assert (this.address != null) : "endpoing address not set";
  }


}
