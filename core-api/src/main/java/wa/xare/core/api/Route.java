package wa.xare.core.api;

import io.vertx.core.Vertx;

public interface Route {

	void setIncomingEndpoint(Endpoint incomingEndpoint);

  Vertx getVertx();

}