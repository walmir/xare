package wa.xare.core;

import io.vertx.core.Vertx;
import wa.xare.core.node.endpoint.Endpoint;

public interface Route {

	void setIncomingEndpoint(Endpoint incomingEndpoint);

  Vertx getVertx();

}