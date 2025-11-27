package com.bug.robot.lambda;

/**
 * Response model for MagicNumberHandler Lambda function.
 * Provides structured response with success status, parsed value, and error details.
 */
public class MagicNumberResponse {
    
    private boolean success;
    private Integer parsedValue;
    private String errorCode;
    private String message;
    
    public MagicNumberResponse() {
    }
    
    private MagicNumberResponse(Builder builder) {
        this.success = builder.success;
        this.parsedValue = builder.parsedValue;
        this.errorCode = builder.errorCode;
        this.message = builder.message;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static MagicNumberResponse success(int parsedValue) {
        return builder()
                .success(true)
                .parsedValue(parsedValue)
                .message("Successfully parsed numeric value")
                .build();
    }
    
    public static MagicNumberResponse error(String errorCode, String message) {
        return builder()
                .success(false)
                .errorCode(errorCode)
                .message(message)
                .build();
    }
    
    // Getters and setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public Integer getParsedValue() {
        return parsedValue;
    }
    
    public void setParsedValue(Integer parsedValue) {
        this.parsedValue = parsedValue;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @Override
    public String toString() {
        return "MagicNumberResponse{" +
                "success=" + success +
                ", parsedValue=" + parsedValue +
                ", errorCode='" + errorCode + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
    
    public static class Builder {
        private boolean success;
        private Integer parsedValue;
        private String errorCode;
        private String message;
        
        public Builder success(boolean success) {
            this.success = success;
            return this;
        }
        
        public Builder parsedValue(Integer parsedValue) {
            this.parsedValue = parsedValue;
            return this;
        }
        
        public Builder errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        
        public MagicNumberResponse build() {
            return new MagicNumberResponse(this);
        }
    }
}
