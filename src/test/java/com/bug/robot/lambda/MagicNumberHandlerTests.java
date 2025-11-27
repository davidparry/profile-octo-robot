package com.bug.robot.lambda;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MagicNumberHandler Lambda handler.
 * Tests cover successful parsing, validation errors, null handling, and edge cases.
 * 
 * These tests verify that the handler:
 * - Does NOT throw NumberFormatException for invalid numeric input
 * - Does NOT throw NullPointerException for null requests or values
 * - Returns structured error responses for all error conditions
 * - Successfully parses valid numeric inputs
 */
class MagicNumberHandlerTests {
    
    private MagicNumberHandler handler;
    
    @BeforeEach
    void setUp() {
        handler = new MagicNumberHandler();
    }
    
    @Test
    @DisplayName("Should successfully parse valid numeric input")
    void shouldSuccessfullyParseValidNumericInput() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("123");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(123, response.getParsedValue());
        assertNull(response.getErrorCode());
        assertNotNull(response.getMessage());
    }
    
    @Test
    @DisplayName("Should return error response for non-numeric input without throwing exception")
    void shouldReturnErrorForNonNumericInput() {
        // Given - the exact input from the bug report
        MagicNumberRequest request = new MagicNumberRequest("123abcert");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getParsedValue());
        assertEquals("INVALID_NUMERIC_INPUT", response.getErrorCode());
        assertTrue(response.getMessage().contains("must contain only digits"));
        assertTrue(response.getMessage().contains("123abcert"));
    }
    
    @Test
    @DisplayName("Should return error response for null request without throwing NullPointerException")
    void shouldReturnErrorForNullRequest() {
        // When
        MagicNumberResponse response = handler.handleRequest(null);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getParsedValue());
        assertEquals("INVALID_REQUEST", response.getErrorCode());
        assertTrue(response.getMessage().contains("Request object must not be null"));
    }
    
    @Test
    @DisplayName("Should return error response for request with null value without throwing NullPointerException")
    void shouldReturnErrorForNullValue() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest(null);
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getParsedValue());
        assertEquals("INVALID_REQUEST", response.getErrorCode());
        assertTrue(response.getMessage().contains("value field must not be null"));
    }
    
    @Test
    @DisplayName("Should return error response for empty string value")
    void shouldReturnErrorForEmptyString() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("INVALID_NUMERIC_INPUT", response.getErrorCode());
        assertTrue(response.getMessage().contains("must not be null or blank"));
    }
    
    @Test
    @DisplayName("Should return error response for blank string value")
    void shouldReturnErrorForBlankString() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("   ");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("INVALID_NUMERIC_INPUT", response.getErrorCode());
        assertTrue(response.getMessage().contains("must not be null or blank"));
    }
    
    @Test
    @DisplayName("Should return error response for alphabetic input")
    void shouldReturnErrorForAlphabeticInput() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("abc");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("INVALID_NUMERIC_INPUT", response.getErrorCode());
        assertTrue(response.getMessage().contains("must contain only digits"));
    }
    
    @Test
    @DisplayName("Should return error response for mixed alphanumeric input")
    void shouldReturnErrorForMixedInput() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("12abc34");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("INVALID_NUMERIC_INPUT", response.getErrorCode());
    }
    
    @Test
    @DisplayName("Should successfully parse numeric string with spaces")
    void shouldParseNumericStringWithSpaces() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("  456  ");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(456, response.getParsedValue());
    }
    
    @Test
    @DisplayName("Should return error for negative number")
    void shouldReturnErrorForNegativeNumber() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("-123");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("INVALID_NUMERIC_INPUT", response.getErrorCode());
    }
    
    @Test
    @DisplayName("Should return error for decimal number")
    void shouldReturnErrorForDecimalNumber() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("123.45");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("INVALID_NUMERIC_INPUT", response.getErrorCode());
    }
    
    @Test
    @DisplayName("Should successfully parse large valid number")
    void shouldParseLargeNumber() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("2147483647");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(2147483647, response.getParsedValue());
    }
    
    @Test
    @DisplayName("Should handle request with context parameter")
    void shouldHandleRequestWithContext() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("999");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request, null);
        
        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(999, response.getParsedValue());
    }
    
    @Test
    @DisplayName("Should return error for special characters")
    void shouldReturnErrorForSpecialCharacters() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("123@#$");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals("INVALID_NUMERIC_INPUT", response.getErrorCode());
    }
    
    @Test
    @DisplayName("Should parse zero successfully")
    void shouldParseZero() {
        // Given
        MagicNumberRequest request = new MagicNumberRequest("0");
        
        // When
        MagicNumberResponse response = handler.handleRequest(request);
        
        // Then
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(0, response.getParsedValue());
    }
}
