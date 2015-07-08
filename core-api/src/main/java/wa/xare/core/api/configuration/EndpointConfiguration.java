package wa.xare.core.api.configuration;

import io.vertx.core.json.JsonObject;
import wa.xare.core.api.Endpoint;
import wa.xare.core.api.EndpointDirection;

public class EndpointConfiguration extends NodeConfiguration {

	private static final String ENDPOINT_ADDRESS_FIELD = "address";
	private static final String ENDPOINT_DIRECTION_FIELD = "direction";
	private static final String ENDPOINT_TYPE_FIELD = "endpointType";

	public EndpointConfiguration() {
    setType(Endpoint.TYPE_NAME);
	}

	public EndpointConfiguration(JsonObject config) {
		this();
		if (config != null) {
			mergeIn(config);
		}
	}

	public EndpointDirection getEndpointDirection() {
		String endpointTypeString = getString(ENDPOINT_DIRECTION_FIELD);
		return EndpointDirection.valueOf(endpointTypeString.toUpperCase());
	}

	public void setEndpointDirection(EndpointDirection endpointDirection) {
    put(ENDPOINT_DIRECTION_FIELD, endpointDirection.name().toLowerCase());
	}

	public String getEndpointType() {
		return getString(ENDPOINT_TYPE_FIELD);
	}

	public void setEndpointType(String typeName) {
    put(ENDPOINT_TYPE_FIELD, typeName);
	}

	public String getEndpointAddress() {
		return getString(ENDPOINT_ADDRESS_FIELD);
	}

	public void setEndpointAddress(String address) {
    put(ENDPOINT_ADDRESS_FIELD, address);
	}

	public EndpointConfiguration withEndpointType(String typeName) {
		setEndpointType(typeName);
		return this;
	}

	public EndpointConfiguration withEndpointAddress(String address) {
		setEndpointAddress(address);
		return this;
	}

	public EndpointConfiguration withEndpointDirection(EndpointDirection direction) {
		setEndpointDirection(direction);
		return this;
	}

}
