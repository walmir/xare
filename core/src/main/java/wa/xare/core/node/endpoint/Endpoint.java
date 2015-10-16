package wa.xare.core.node.endpoint;

import wa.xare.core.node.Node;
import wa.xare.core.packet.Packet;

public interface Endpoint extends Node {

  public static final String TYPE_NAME = "endpoint";
  public static final String ENDPOINT_TYPE_FIELD = "endpointType";

	void deploy();

	void setHandler(EndpointHandler handler);

  public interface EndpointHandler {

    void handleIncomingPacket(Packet packet);

  }

}
