package com.central.reward_service.service;

import org.springframework.http.ResponseEntity;
import org.openapitools.model.RewardRuleRequest;
import org.openapitools.model.RewardRuleResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RewardRuleService {
    ResponseEntity<List<RewardRuleResponse>> adminRewardRulesBulkPost(List<RewardRuleRequest> rewardRuleRequest);

    ResponseEntity<List<RewardRuleResponse>> adminRewardRulesGet();

    ResponseEntity<Void> adminRewardRulesIdDelete(Long id);

    ResponseEntity<RewardRuleResponse> adminRewardRulesIdGet(Long id);

    ResponseEntity<RewardRuleResponse> adminRewardRulesIdPut(Long id, RewardRuleRequest rewardRuleRequest);

    ResponseEntity<RewardRuleResponse> adminRewardRulesPost(RewardRuleRequest rewardRuleRequest);

    ResponseEntity<List<RewardRuleResponse>> adminRewardRulesTierTierNameGet(String tierName);
}
