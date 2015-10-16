package wa.xare.core.builder.mocks;

import wa.xare.core.annotation.Component;

@Component(discriminator = IMockComponent.DISCRIMINATOR_NAME)
public interface IMockComponent {

  final String DISCRIMINATOR_NAME = "discField";

}
