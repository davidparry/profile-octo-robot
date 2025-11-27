package com.bug.robot.lambda;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InputParser utility class.
 * Tests cover valid inputs, invalid inputs, null handling, and edge cases.
 */
class InputParserTests {
    
    @Test
    @DisplayName("Should successfully parse valid numeric string")
    void shouldParseValidNumericString() {
        // Given
        String input = "123";
        
        // When
        int result = InputParser.parseRequiredInt(input, "value");
        
        // Then
        assertEquals(123, result);
    }
    
    @Test
    @DisplayName("Should successfully parse numeric string with leading/trailing spaces")
    void shouldParseNumericStringWithSpaces() {
        // Given
        String input = "  456  ";
        
        // When
        int result = InputParser.parseRequiredInt(input, "value");
        
        // Then
        assertEquals(456, result);
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for non-numeric input")
    void shouldThrowExceptionForNonNumericInput() {
        // Given
        String input = "123abcert";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseRequiredInt(input, "value")
        );
        
        assertTrue(exception.getMessage().contains("must contain only digits"));
        assertTrue(exception.getMessage().contains("123abcert"));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for null input")
    void shouldThrowExceptionForNullInput() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseRequiredInt(null, "value")
        );
        
        assertTrue(exception.getMessage().contains("must not be null or blank"));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for empty string")
    void shouldThrowExceptionForEmptyString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseRequiredInt("", "value")
        );
        
        assertTrue(exception.getMessage().contains("must not be null or blank"));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for blank string")
    void shouldThrowExceptionForBlankString() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseRequiredInt("   ", "value")
        );
        
        assertTrue(exception.getMessage().contains("must not be null or blank"));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for alphabetic input")
    void shouldThrowExceptionForAlphabeticInput() {
        // Given
        String input = "abc";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseRequiredInt(input, "value")
        );
        
        assertTrue(exception.getMessage().contains("must contain only digits"));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for mixed alphanumeric input")
    void shouldThrowExceptionForMixedInput() {
        // Given
        String input = "12abc34";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseRequiredInt(input, "value")
        );
        
        assertTrue(exception.getMessage().contains("must contain only digits"));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for negative number string")
    void shouldThrowExceptionForNegativeNumber() {
        // Given - negative numbers have a '-' character which is not a digit
        String input = "-123";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseRequiredInt(input, "value")
        );
        
        assertTrue(exception.getMessage().contains("must contain only digits"));
    }
    
    @Test
    @DisplayName("Should throw IllegalArgumentException for decimal number string")
    void shouldThrowExceptionForDecimalNumber() {
        // Given
        String input = "123.45";
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseRequiredInt(input, "value")
        );
        
        assertTrue(exception.getMessage().contains("must contain only digits"));
    }
    
    @Test
    @DisplayName("Should parse large valid number")
    void shouldParseLargeNumber() {
        // Given
        String input = "2147483647"; // Integer.MAX_VALUE
        
        // When
        int result = InputParser.parseRequiredInt(input, "value");
        
        // Then
        assertEquals(2147483647, result);
    }
    
    @Test
    @DisplayName("Should return null for optional int with null input")
    void shouldReturnNullForOptionalIntWithNullInput() {
        // When
        Integer result = InputParser.parseOptionalInt(null, "value");
        
        // Then
        assertNull(result);
    }
    
    @Test
    @DisplayName("Should return null for optional int with blank input")
    void shouldReturnNullForOptionalIntWithBlankInput() {
        // When
        Integer result = InputParser.parseOptionalInt("  ", "value");
        
        // Then
        assertNull(result);
    }
    
    @Test
    @DisplayName("Should parse valid number for optional int")
    void shouldParseValidNumberForOptionalInt() {
        // Given
        String input = "789";
        
        // When
        Integer result = InputParser.parseOptionalInt(input, "value");
        
        // Then
        assertNotNull(result);
        assertEquals(789, result);
    }
    
    @Test
    @DisplayName("Should throw exception for invalid optional int")
    void shouldThrowExceptionForInvalidOptionalInt() {
        // Given
        String input = "123abc";
        
        // When & Then
        assertThrows(
            IllegalArgumentException.class,
            () -> InputParser.parseOptionalInt(input, "value")
        );
    }
}
