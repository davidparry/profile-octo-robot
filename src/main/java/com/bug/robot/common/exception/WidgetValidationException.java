package com.bug.robot.common.exception;

public class WidgetValidationException extends RuntimeException {
    
    public WidgetValidationException(String message) {
        super(message);
    }
    
    public WidgetValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
