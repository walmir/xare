package wa.xare.core.node.endpoint;

import io.vertx.core.http.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import wa.xare.core.Route;
import wa.xare.core.annotation.EndpointType;
import wa.xare.core.annotation.Field;
import wa.xare.core.packet.DefaultPacket;
import wa.xare.core.packet.Packet;

@EndpointType
public class HttpEndpoint extends AbstractEndpoint {

  @Field
  private int port = HttpServerOptions.DEFAULT_PORT;

  @Field
  private String host = HttpServerOptions.DEFAULT_HOST;

  @Field
  private String path;

  @Field
  private String method = "GET";


  private HttpServer server;
  private HttpClient client;
  private Router httpRouter;

  protected HttpEndpoint(Route route, EndpointDirection direction) {
    super(direction);
    this.route = route;
	}

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public HttpServer getServer() {
    return server;
  }

  public void setServer(HttpServer server) {
    this.server = server;
  }

  public Router getHttpRouter() {
    return httpRouter;
  }

  public void setHttpRouter(Router httpRouter) {
    this.httpRouter = httpRouter;
  }

  @Override
	protected void deliverOutgoingMessage(Object message) {
    client.request(
        method != null ? HttpMethod.valueOf(method) : HttpMethod.GET, port,
        host, path);
	}

  @Override
	protected void deployAsOutgoingEndpoint() {
    client = route.getVertx().createHttpClient();
	}

	@Override
	protected void deployAsIncomingEndpoint() {
    server = route.getVertx().createHttpServer();
    httpRouter = Router.router(route.getVertx());
    httpRouter.route().handler(BodyHandler.create());
    httpRouter.route().method(HttpMethod.valueOf(method))
        .path(path).handler(ctx -> {
          // buildPacket
            Packet packet = buildPacket(ctx);
            packet.setBody(ctx.getBodyAsString());
            this.notifyHandler(packet);
        });

    server.requestHandler(httpRouter::accept).listen(port, host);
	}

  private Packet buildPacket(RoutingContext ctx) {
    HttpServerRequest request = ctx.request();

    Packet packet = new DefaultPacket();
    JsonObject packetBody = new JsonObject();

    packet.addHeader("httpMethod", request.method().name());

    // add http headers
    if (!request.headers().isEmpty()){
      JsonObject headers = new JsonObject();
      request.headers().forEach(e -> headers.put(e.getKey(), e.getValue()));
      packet.addHeader("httpHeaders", headers);
    }

    // add path parameters
    if(!request.params().isEmpty()){
      JsonObject params = new JsonObject();
      request.params().forEach(e -> params.put(e.getKey(), e.getValue()));
      packetBody.put("pathParameters", params);
    }

    String bodyString = ctx.getBodyAsString();
    if (!bodyString.isEmpty()) {
      packetBody.put("requestBody", bodyString);
    }

    packet.setBody(packetBody);

    return packet;
  }

}
