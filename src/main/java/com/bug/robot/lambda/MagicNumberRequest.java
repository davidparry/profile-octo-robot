package com.bug.robot.lambda;

/**
 * Request model for MagicNumberHandler Lambda function.
 * Represents the input payload containing a value to be parsed as an integer.
 */
public class MagicNumberRequest {
    
    private String value;
    
    public MagicNumberRequest() {
    }
    
    public MagicNumberRequest(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "MagicNumberRequest{" +
                "value='" + value + '\'' +
                '}';
    }
}
