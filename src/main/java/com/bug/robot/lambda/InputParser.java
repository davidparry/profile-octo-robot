package com.bug.robot.lambda;

import java.util.regex.Pattern;

/**
 * Utility class for safe parsing and validation of input strings.
 * Provides defensive parsing methods that validate input before conversion
 * to prevent NumberFormatException and other parsing errors.
 * 
 * Following Single Responsibility Principle - this class is solely responsible
 * for input validation and parsing logic.
 */
public class InputParser {
    
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    
    /**
     * Parses a required integer field from a string input with validation.
     * 
     * @param raw the raw string value to parse
     * @param fieldName the name of the field (for error messages)
     * @return the parsed integer value
     * @throws IllegalArgumentException if the input is null, blank, or contains non-numeric characters
     */
    public static int parseRequiredInt(String raw, String fieldName) {
        // Null and blank validation
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException(
                String.format("%s must not be null or blank", fieldName)
            );
        }
        
        // Numeric pattern validation
        if (!NUMERIC_PATTERN.matcher(raw.trim()).matches()) {
            throw new IllegalArgumentException(
                String.format("%s must contain only digits. Invalid value: '%s'", fieldName, raw)
            );
        }
        
        // Safe parsing - at this point we know it's a valid numeric string
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            // This should rarely happen given our validation, but handle it defensively
            throw new IllegalArgumentException(
                String.format("%s contains an invalid numeric value: '%s'", fieldName, raw), e
            );
        }
    }
    
    /**
     * Attempts to parse an optional integer field from a string input.
     * Returns null if the input is null or blank.
     * 
     * @param raw the raw string value to parse
     * @param fieldName the name of the field (for error messages)
     * @return the parsed Integer value, or null if input is null/blank
     * @throws IllegalArgumentException if the input contains non-numeric characters
     */
    public static Integer parseOptionalInt(String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return parseRequiredInt(raw, fieldName);
    }
}
