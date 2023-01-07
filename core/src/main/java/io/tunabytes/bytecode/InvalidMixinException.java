package io.tunabytes.bytecode;

public class InvalidMixinException extends RuntimeException {

    public InvalidMixinException(String message) {
        super(message);
    }

    public InvalidMixinException(String message, Throwable cause) {
        super(message, cause);
    }
}