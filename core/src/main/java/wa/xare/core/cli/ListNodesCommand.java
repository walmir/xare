package wa.xare.core.cli;


import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.DefaultValue;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.launcher.DefaultCommand;
import wa.xare.core.XareCoreVerticle;

@Name("listNodes")
public class ListNodesCommand extends XareDefaultCommand {

  private String address;

  @Option(longName = "address", shortName = "a")
  @DefaultValue(XareCoreVerticle.DEFAULT_ADDRESS)
  public void setAddress(String address) {
    this.address = address;
  }

//  @Override
//  public void run() throws CLIException {
//    JsonObject command = new JsonObject();
//
//    DeliveryOptions opts = new DeliveryOptions();
//    opts.addHeader("command", "listNodes");
//
//    VertxOptions options = new VertxOptions();
//
//    Vertx.clusteredVertx(options, res -> {
//      if (res.succeeded()) {
//        Vertx vertx = res.result();
//        EventBus eventBus = vertx.eventBus();
//        eventBus.send(address, command, opts, resp -> {
//          if (resp.succeeded()) {
//            System.out.println("results:" + resp.result().body());
//          } else {
//            System.out.println(resp.cause().getMessage());
//          }
//          vertx.close();
//        });
//        System.out.println("We now have a clustered event bus: " + eventBus);
//      } else {
//        System.out.println("Failed: " + res.cause().getMessage());
//      }
//    });
//
//  }

//  @Override
  protected Object buildMessage() {
    return null;
  }

  @Override
  protected void handleCommandResponse(Message<Object> result) {

  }

  @Override
  protected void handleFailedCommand(Throwable cause) {

  }
}
