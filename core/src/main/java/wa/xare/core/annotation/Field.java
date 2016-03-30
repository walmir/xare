package wa.xare.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Field {

  /** Sets a different name for the field for use in configuration instead of the actual java field name */
  String value() default "";

  String type() default "string";

  /** Defines if the field required. Default is true. */
  boolean required() default true;
}
