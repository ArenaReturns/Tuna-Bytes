package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotated method will return the instance of the type of the class specified with {@link #value()}               <br>
 * to maintain the runtime typing of a private class without resorting to reflection (which breaks remapping)       <br>
 * Ex: in a {@link Mixin} class the following code
 * <pre>{@code
 *      @PrivateClassAccessor("com.example.PrivateClass")
 *      public Class privateClassAccessor() {
 *          return null
 *      }
 * }</pre>
 * </code>
 * Will be transformed to:
 * <pre>{@code
 *     public Class privateClassAccessor() {
 *         return com.example.PrivateClass.class;
 *     }
 * }</pre>
 * Make sure that the mixinified class has access to the provided {@link #value()} otherwise you will get an {@link IllegalAccessError} !
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PrivateClassAccessor {
    String value();
}
