package com.central.reward_service.service;

import org.openapitools.model.RewardRequest;
import org.openapitools.model.RewardResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Service interface for reward management operations.
 * Handles reward processing, retrieval, and management.
 */
public interface RewardService {

    ResponseEntity<RewardResponse> getRewardById(Long rewardId);
    ResponseEntity<List<RewardResponse>> getUserRewards(String userId, Integer page, Integer size);
    ResponseEntity<RewardResponse> processTransaction(RewardRequest request);

}
