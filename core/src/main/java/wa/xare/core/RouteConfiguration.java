package wa.xare.core;

import java.util.ArrayList;
import java.util.List;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.node.NodeConfiguration;
import wa.xare.core.node.endpoint.EndpointConfiguration;

@SuppressWarnings("serial")
public class RouteConfiguration extends JsonObject {

	public static final String NAME_FIELD = "name";
	public static final String INCOMING_ENDPOINT_FIELD = "from";
	public static final String NODE_CONFIGS_FIELD = "nodes";

	public RouteConfiguration() {
	}

	public RouteConfiguration(JsonObject config) {
		mergeIn(config);
	}

	public void setName(String name) {
		this.putString(NAME_FIELD, name);
	}

	public String getName() {
		return this.getString(NAME_FIELD);
	}

	public void setIncomingEndpointConfiguration(
	    EndpointConfiguration incomintEndpoint) {
		putObject(INCOMING_ENDPOINT_FIELD, incomintEndpoint);
	}

	public EndpointConfiguration getIncomingEndpointConfiguration() {
		return new EndpointConfiguration(this.getObject(INCOMING_ENDPOINT_FIELD));
	}

	public void setNodeConfigurations(JsonArray nodeConfigurations) {
		putArray(NODE_CONFIGS_FIELD, nodeConfigurations);
	}

	public void addNodeConfiguration(NodeConfiguration nodeConfig) {
		JsonArray array = getArray(NODE_CONFIGS_FIELD);
		if (array == null) {
			array = new JsonArray();
		}
		array.add(nodeConfig);
		this.putArray(NODE_CONFIGS_FIELD, array);

	}

	public List<NodeConfiguration> getNodeConfigurations() {
		JsonArray array = getArray(NODE_CONFIGS_FIELD);
		List<NodeConfiguration> list = new ArrayList<>();
		if (array != null) {
			array.forEach(config -> {
				list.add(new NodeConfiguration((JsonObject) config));
			});
		}
		return list;
	}
}
