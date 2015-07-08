package wa.xare.core.api;


public interface Endpoint extends Node {

  public static final String TYPE_NAME = "endpoint";

	void deploy();

	void setHandler(EndpointHandler handler);

  public interface EndpointHandler {

    void handleIncomingPacket(Packet packet);

  }

}
