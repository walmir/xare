package wa.xare.core.cli;

import io.vertx.core.spi.launcher.Command;
import io.vertx.core.spi.launcher.CommandFactory;
import io.vertx.core.spi.launcher.DefaultCommandFactory;

/**
 * Created by wajdi on 16/03/16.
 */
public class HelloCommandFactory extends DefaultCommandFactory<HelloCommand>{

  /**
   * Creates a new {@link CommandFactory}.
   */
  public HelloCommandFactory() {
    super(HelloCommand.class);
  }
}
