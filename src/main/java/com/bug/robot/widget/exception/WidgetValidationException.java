package com.bug.robot.widget.exception;

/**
 * Exception thrown when widget validation fails.
 */
public class WidgetValidationException extends RuntimeException {

    public WidgetValidationException(String message) {
        super(message);
    }

    public WidgetValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
