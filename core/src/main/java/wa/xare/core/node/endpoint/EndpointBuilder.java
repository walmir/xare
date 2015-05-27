package wa.xare.core.node.endpoint;

import wa.xare.core.Route;

public class EndpointBuilder {

  private Route route;

  public EndpointBuilder(Route defaultRoute) {
		this.route = defaultRoute;
	}

	public Endpoint buildEndpoint(EndpointConfiguration config) {
		route.getContainer().logger().debug("building endpoint: " + config);
		EndpointDirection direction = config.getEndpointDirection();
		String address = config.getEndpointAddress();
		String type = config.getEndpointType();

		Endpoint endpoint = null;
		switch (type) {
		case EndpointTypeNames.DEFAULT_DIRECT_ENDPOINT:
			endpoint = new DirectEndpoint(route, direction, address);
			break;
		}
		return endpoint;
	}

}
