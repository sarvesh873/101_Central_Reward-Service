package com.central.reward_service.constants;

public class Constants {
    // Error Messages
    public static final String TRANSACTION_ALREADY_REWARDED = "Transaction already rewarded";
    public static final String REWARD_NOT_FOUND = "Reward not found with ID: ";
    public static final String REWARD_CLAIM_ERROR = "Error processing reward claim";
    public static final String REWARD_ALREADY_CLAIMED = "Reward already claimed or expired";
    public static final String INVALID_REWARD_STATE = "Reward is in an invalid state: ";
    public static final String NO_REWARD_TIER = "Transaction amount does not fall into any configured reward tier";
    public static final String NO_REWARDS_CONFIGURED = "Configuration Error: No rewards found for this tier";
    
    // Log Messages
    public static final String LOG_REWARD_PROCESSING_START = "Starting reward processing for transaction: {}";
    public static final String LOG_REWARD_PROCESSED = "Successfully processed reward for transaction: {}";
    public static final String LOG_REWARD_CLAIMED = "Reward claimed successfully - ID: {}";
    public static final String LOG_REWARD_EXPIRED = "Reward has expired - ID: {}";
    public static final String LOG_TRANSACTION_PROCESSING = "Processing transaction amount: {}";
    
    // Error Codes
    public static final double ERROR_CODE_DUPLICATE_TRANSACTION = 400.06;
    public static final double ERROR_CODE_DATA_INTEGRITY = 400.02;
    public static final double ERROR_CODE_REWARD_CLAIM = 404.06;
    
    // Error Descriptions
    public static final String ERROR_DESC_DUPLICATE_TRANSACTION = "Transaction has already been awarded a reward";
    public static final String ERROR_DESC_DATA_INTEGRITY = "Data integrity violation";
    public static final String ERROR_DESC_REWARD_CLAIM = "Reward Already claimed or Reward expired";
}
