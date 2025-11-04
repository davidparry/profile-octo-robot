package com.bug.robot.widget.exception;

public class ProfileNotFoundException extends RuntimeException {
    
    public ProfileNotFoundException(Long profileId) {
        super("Profile not found with id: " + profileId);
    }
    
    public ProfileNotFoundException(String message) {
        super(message);
    }
}
