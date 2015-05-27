package wa.xare.core.selector;

import net.minidev.json.JSONArray;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonElement;
import org.vertx.java.core.json.JsonObject;

import wa.xare.core.packet.Packet;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

public class JsonPathSelector extends AbstractSelector {

  public JsonPathSelector(String expression) {
    super(expression);
  }

  @Override
  public Object getSelection(Packet packet) {

    JsonElement object = null;

    switch (getSegment()) {

    case HEADERS:
      final JsonObject headerObject = new JsonObject();
      packet.getHeaders().forEach((k, v) -> {
        headerObject.putString(k, v);
      });
      object = headerObject;
      break;
    default: // Default is BODY
      object = (JsonElement) packet.getBody();
      break;
    }

    Configuration conf = Configuration.defaultConfiguration();
    conf.setOptions(Option.ALWAYS_RETURN_LIST);
    System.out.println(object);
    Object selection = JsonPath.using(conf).parse(object.toString())
        .read(getExpression());

    if (selection != null) {
      if (selection instanceof JSONArray){
        JSONArray array = (JSONArray) selection;
        return new JsonArray(array.toJSONString());
      } else if (selection instanceof String){
        return selection;
      }
    }

    return null;
    // Object selection = object == null ? null : JsonPath.using(conf).read(
    // object.toString(),
    // getExpression());


  }

}
