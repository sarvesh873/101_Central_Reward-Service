package com.central.reward_service.exception;

/**
 * Custom exception used for reward claiming business rule violations,
 * such as expired, already claimed, or not found rewards.
 */
public class RewardClaimException extends RuntimeException {
    public RewardClaimException(String message) {
        super(message);
    }
}