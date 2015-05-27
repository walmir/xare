package wa.xare.core;

import org.vertx.java.core.Vertx;
import org.vertx.java.platform.Container;

import wa.xare.core.node.endpoint.Endpoint;

public interface Route {

	void setIncomingEndpoint(Endpoint incomingEndpoint);

	Container getContainer();

  Vertx getVertx();

}