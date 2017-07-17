package wa.xare.core;

import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import wa.xare.core.node.PipelineNode;
import wa.xare.core.node.endpoint.Endpoint;

public interface Route extends Verticle {

  void setName(String name);

	void setIncomingEndpoint(Endpoint incomingEndpoint);

  void setPipeline(PipelineNode pipeline);

  Vertx getVertx();

  String getDeploymentId();

  // HttpServer getServer();

}
