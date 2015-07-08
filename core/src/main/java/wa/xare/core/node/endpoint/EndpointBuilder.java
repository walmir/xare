package wa.xare.core.node.endpoint;

import wa.xare.core.api.Route;

public class EndpointBuilder {

  private Route route;

  private EndpointBuilder(Route defaultRoute) {
		this.route = defaultRoute;
	}

  // public Endpoint buildEndpoint(EndpointConfiguration config) {
  // route.getContainer().logger().debug("building endpoint: " + config);
  // EndpointDirection direction = config.getEndpointDirection();
  // String address = config.getEndpointAddress();
  // String type = config.getEndpointType();
  //
  // Endpoint endpoint = null;
  // switch (type) {
  // case EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT:
  // endpoint = new DirectEndpoint(direction, address);
  // endpoint.setRoute(route);
  // break;
  // }
  // return endpoint;
  // }

}
