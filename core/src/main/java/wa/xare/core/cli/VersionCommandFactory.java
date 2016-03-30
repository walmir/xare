package wa.xare.core.cli;

import io.vertx.core.spi.launcher.DefaultCommandFactory;

public class VersionCommandFactory extends DefaultCommandFactory<VersionCommand> {
  public VersionCommandFactory() {
    super(VersionCommand.class);
  }
}
