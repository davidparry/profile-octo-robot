package com.bug.robot.lambda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AWS Lambda handler for processing magic number requests.
 * 
 * This handler demonstrates proper input validation and error handling
 * to prevent NumberFormatException and NullPointerException issues.
 * 
 * Following SOLID principles:
 * - Single Responsibility: Handles request validation and delegates parsing to InputParser
 * - Dependency Inversion: Depends on abstractions (InputParser utility)
 * - Open-Closed: Can be extended with additional validation rules without modification
 * 
 * Error Handling Strategy:
 * - Validates all inputs before processing
 * - Returns structured error responses instead of throwing exceptions
 * - Prevents NullPointerException through explicit null checks
 * - Logs appropriately based on error severity
 */
public class MagicNumberHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(MagicNumberHandler.class);
    
    /**
     * Handles the Lambda request with robust error handling.
     * 
     * @param request the incoming request containing a value to parse
     * @return structured response with success status and parsed value or error details
     */
    public MagicNumberResponse handleRequest(MagicNumberRequest request) {
        logger.info("Processing magic number request: {}", request);
        
        try {
            // Null-safety: Validate request object
            if (request == null) {
                logger.warn("Received null request object");
                return MagicNumberResponse.error(
                    "INVALID_REQUEST",
                    "Request object must not be null"
                );
            }
            
            // Null-safety: Validate value field
            String value = request.getValue();
            if (value == null) {
                logger.warn("Received request with null value field");
                return MagicNumberResponse.error(
                    "INVALID_REQUEST",
                    "Request value field must not be null"
                );
            }
            
            // Delegate to InputParser for safe parsing with validation
            int parsedValue = InputParser.parseRequiredInt(value, "value");
            
            logger.info("Successfully parsed value: {}", parsedValue);
            return MagicNumberResponse.success(parsedValue);
            
        } catch (IllegalArgumentException e) {
            // Expected validation errors - log at WARN level without stack trace
            logger.warn("Validation error: {}", e.getMessage());
            return MagicNumberResponse.error(
                "INVALID_NUMERIC_INPUT",
                e.getMessage()
            );
            
        } catch (Exception e) {
            // Unexpected errors - log at ERROR level with stack trace
            logger.error("Unexpected error processing request", e);
            return MagicNumberResponse.error(
                "INTERNAL_ERROR",
                "An unexpected error occurred while processing the request"
            );
        }
    }
    
    /**
     * Alternative handler signature compatible with AWS Lambda RequestHandler interface.
     * This method can be used when deploying as an actual Lambda function.
     * 
     * @param request the incoming request
     * @param context the Lambda context (can be null for testing)
     * @return structured response
     */
    public MagicNumberResponse handleRequest(MagicNumberRequest request, Object context) {
        // Context is available but not used in this simple handler
        // In production, context could be used for logging request ID, remaining time, etc.
        return handleRequest(request);
    }
}
