package wa.xare.sender;

import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import java.util.ArrayList;
import java.util.List;

/*
This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 */
public class SenderVerticle extends Verticle {

  String jsonString =
          "{\n" +
          "  \"someField\": \"someValue\",\n" +
          "  \"books\": [\n" +
          "    {\n" +
          "      \"title\": \"The Jungle Book\",\n" +
          "      \"author\": \"Rudyard Kipling\"\n" +
          "    },\n" +
          "    {\n" +
          "      \"title\": \"Demian\",\n" +
          "      \"author\": \"Hermann Hesse\"\n" +
          "    }\n" +
          "  ]\n" +
          "}";


  public void start() {
    final List<Object> list = new ArrayList<>();
    Object msgBody = new JsonObject(jsonString);

    vertx.eventBus().send("address-0", msgBody);
    container.logger().info("Sender Verticle done.");
  }
}
