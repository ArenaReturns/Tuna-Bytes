package io.tunabytes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a method snap that is injected at a specific position in
 * another method.
 * <p>
 * Note: Injected code will be directly translated into the targeted method, and not wrapped
 * into an external method invocation. For example, any {@code return} in the injected code
 * will translate into a {@code return} in the target method.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {

    /**
     * The method name that is being targeted, Default to the method's name
     *
     * @return The target method name to inject into.
     */
    String method() default "";

    /**
     * The return type of the method that is being targeted, Default to the return type of the mixin method
     *
     * @return The target method type to inject into.
     */
    String returnType() default "";

//    /**
//     * A complete descriptor representation of the method to help
//     * matching the current method. If not use, the first method with the given
//     * name will be used
//     *
//     * ex:
//     * target method is <code>void foo()</code> descriptor must be <code>()V</code>
//     * target method is <code>int[] bar(int i, String s)</code> descriptor must be <code>(ILjava/lang/String;)[I</code>
//     *
//     * @link https://asm.ow2.io/asm4-guide.pdf section 2.1.4 for more example
//     *
//     * @return
//     */
//    String descriptor() default "";

    /**
     * Where the injection will occur
     *
     * @return Injection position.
     * @see At
     */
    At at();
    
//    AtQuery[] atQuery() default {};

    /**
     * The line number for injection. This must be used in tandem
     * with {@link At#BEFORE_LINE} or {@link At#AFTER_LINE}.
     * <p>
     * Note: The line number must match the number in the source code. Since
     * line numbers are preserved into the bytecode as instructions when the class is
     * compiled, they will be used as a reference point. Binary lines will not work and
     * most likely going to yield incorrect lines.
     *
     * @return The line number to inject before of after.
     */
    int lineNumber() default 0;

    /**
     * Used with {@link At#REPLACE_LINE} to specify a range of lines to be replaced
     *  where the inclusive range is defined by [lineNumber,lineNumberEnd].
     * <p>
     * Note: The line number must match the number in the source code. Since
     * line numbers are preserved into the bytecode as instructions when the class is
     * compiled, they will be used as a reference point. Binary lines will not work and
     * most likely going to yield incorrect lines.
     * @return The last line number to be replaced
     */
    int injectLineReplaceEnd() default -1;

    /**
    * By default, the last return of the method is not copied from the mixins 
    *  method to the edited class as it can be implicitly put at the end of 
    *  the method by the compiler.
    * If you want to return to be copied set this to 'true'.
    * @return
    */
    boolean keepLastReturn() default false;

    /**
     * Represents an injection point
     */
    enum At {

        /**
         * Injects at the very beginning of the method
         */
        BEGINNING,

        /**
         * Injects at the very end of the method. This will be invoked just before
         * the last return statement.
         */
        END,

        /**
         * Injects before each {@code return} in the method. This should be used in methods
         * that have complex returning flow, in which it is desired to call the injection
         * after the method has finished executing.
         * <p>
         * Note that, even if no return is actually in the source in void methods,
         * there is an invisible return at the end of the void method.
         */
        BEFORE_EACH_RETURN,

        /**
         * Injects before the given line in {@link Inject#lineNumber()}.
         *
         * @see Inject#lineNumber()
         */
        BEFORE_LINE,

        /**
         * Replace the given line
         *
         * @deprecated EXPERIMENTAL make sure to test your bytecode !
         * @see Inject#lineNumber()
         */
        @Deprecated
        REPLACE_LINE,

        /**
         * Injects after the given line in {@link Inject#lineNumber()}.
         *
         * @see Inject#lineNumber()
         */
        AFTER_LINE,

//        /**
//         * @see Inject#atQuery()
//         */
//        QUERY
        ;

        private static final Map<String, At> BY_NAME = new HashMap<>();

        public static At at(String at) { return BY_NAME.get(at); }

        static { for (At at : values()) BY_NAME.put(at.name(), at); }
    }

}
