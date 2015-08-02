package wa.xare.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EndpointType {

  /**
   * Defines the type of the endpoint. If not set the name of the class is used
   * instead.
   */
  String value() default "";

}
