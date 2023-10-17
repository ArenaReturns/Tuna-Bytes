package io.tunabytes;

/**
 * Ex: (where: "INVOKE", target: "")
 */
public @interface AtQuery {
    
    int ordinal() default -1;
    int minOrdinal() default -1;
    int maxOrdinal() default -1;
    
    int requiredMatchCount() default 1;
    
    String where();

    String target();
    
    
    
    String INVOKE = "INVOKE";
}
