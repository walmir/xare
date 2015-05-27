package wa.xare.core.node.endpoint;

import wa.xare.core.node.Node;

public interface Endpoint extends Node {

	void deploy();

	void setHandler(EndpointHandler handler);

}
