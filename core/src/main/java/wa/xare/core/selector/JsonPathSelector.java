package wa.xare.core.selector;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.minidev.json.JSONArray;
import wa.xare.core.packet.Packet;

import com.jayway.jsonpath.JsonPath;

public class JsonPathSelector extends AbstractSelector {

  public JsonPathSelector(String expression) {
    super(expression);
  }

  @Override
  public Object getSelection(Packet packet) {
    Object selectedSegment = getSelectionSegment(packet);
    Object selection = JsonPath.read(selectedSegment.toString(),
        getExpression());

    return prepareSelection(selection);
  }

  private Object prepareSelection(Object selection) {
    if (selection != null) {
      if (selection instanceof JSONArray) {
        JSONArray array = (JSONArray) selection;
        return new JsonArray(array.toJSONString());
      } else if (selection instanceof String) {
        return selection;
      }
    }
    return null;
  }

  private Object getSelectionSegment(Packet packet) {
    Object object = null;

    switch (getSegment()) {

    case HEADERS:
      final JsonObject headerObject = new JsonObject();
      packet.getHeaders().forEach((k, v) -> {
        headerObject.put(k, v);
      });
      object = headerObject;
      break;
    default: // Default is BODY
      object = packet.getBody();
      break;
    }

    return object;
  }

}
