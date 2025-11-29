package com.central.reward_service.exception;

public class RewardNotFoundException extends RuntimeException {
    public RewardNotFoundException(String message) {
        super(message);
    }
}
