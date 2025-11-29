package com.central.reward_service.controller;

import org.openapitools.model.RewardClaimResponse;
import com.central.reward_service.service.RewardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.openapitools.api.RewardManagementApi;
import org.openapitools.model.RewardResponse;
import org.openapitools.model.RewardRequest;

import java.util.List;

/**
 * REST controller for managing rewards.
 * Implements rate limiting, input validation, and proper error handling.
 */
@Slf4j
@RestController
public class RewardController implements RewardManagementApi {

    @Autowired
    private RewardService rewardService;

    @Override
    public ResponseEntity<RewardResponse> getRewardById(Long rewardId) {
        return rewardService.getRewardById(rewardId);
    }

    @Override
    public ResponseEntity<List<RewardResponse>> getUserRewards(String userId, Integer page, Integer size) {
        return rewardService.getUserRewards(userId, page, size);
    }

    @Override
    public ResponseEntity<RewardResponse> processTransaction(RewardRequest request) {
        return rewardService.processTransaction(request);
    }

    @Override
    public ResponseEntity<RewardClaimResponse> claimReward( Long rewardId) {
        return rewardService.claimReward(rewardId);
    }
}
