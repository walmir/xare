package wa.xare.core.cli;

import io.vertx.core.cli.CLIException;
import io.vertx.core.cli.annotations.Name;
import io.vertx.core.cli.annotations.Option;
import io.vertx.core.spi.launcher.DefaultCommand;

/**
 * Created by wajdi on 16/03/16.
 */
@Name("hello")
public class HelloCommand extends DefaultCommand{

  private String name;

  @Option(shortName = "n")
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public void run() throws CLIException {
    System.out.println("hello " + name + getClass().getPackage().getImplementationVersion());
  }
}
