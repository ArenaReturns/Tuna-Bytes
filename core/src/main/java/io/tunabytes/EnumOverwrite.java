package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to overwrite an enum constant, if no enum is found with the name provided;
 * a new enum constant is created.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumOverwrite {

    /**
     * The name of the enum constant being overwritten. Default to the field name
     *
     * @return The method name
     */
    String value() default "";

    /**
     * The names of the enum constants that will be removed.
     */
    String[] deletedEnumConstants() default "";

}