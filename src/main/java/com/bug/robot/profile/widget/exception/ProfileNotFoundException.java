package com.bug.robot.profile.widget.exception;

/**
 * Exception thrown when a profile is not found in the system.
 * Used for proper HTTP 404 error handling in the widget API.
 */
public class ProfileNotFoundException extends RuntimeException {

    public ProfileNotFoundException(String message) {
        super(message);
    }

    public ProfileNotFoundException(Long profileId) {
        super("Profile not found with ID: " + profileId);
    }
}
