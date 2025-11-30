package com.central.reward_service.controller;

import com.central.reward_service.service.RewardRuleService;
import org.openapitools.api.RewardRuleManagementApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.openapitools.model.RewardRuleResponse;
import org.openapitools.model.RewardRuleRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RewardRuleController implements  RewardRuleManagementApi {

    @Autowired
    private RewardRuleService rewardRuleService;

    /**
     * POST /admin/reward-rules/bulk : Bulk create reward rules
     */
    @Override
    public ResponseEntity<List<RewardRuleResponse>> adminRewardRulesBulkPost(List<RewardRuleRequest> rewardRuleRequest) {
        return rewardRuleService.adminRewardRulesBulkPost(rewardRuleRequest);
    }

    //---------------------------------------------------------

    /**
     * GET /admin/reward-rules : Get all reward rules
     */
    @Override
    public ResponseEntity<List<RewardRuleResponse>> adminRewardRulesGet() {
        return rewardRuleService.adminRewardRulesGet();
    }

    //---------------------------------------------------------

    /**
     * DELETE /admin/reward-rules/{id} : Delete a reward rule
     */
    @Override
    public ResponseEntity<Void> adminRewardRulesIdDelete(Long id) {
        return rewardRuleService.adminRewardRulesIdDelete(id);
    }

    //---------------------------------------------------------

    /**
     * GET /admin/reward-rules/{id} : Get a reward rule by ID
     */
    @Override
    public ResponseEntity<RewardRuleResponse> adminRewardRulesIdGet(Long id) {
        return rewardRuleService.adminRewardRulesIdGet(id);
    }

    //---------------------------------------------------------

    /**
     * PUT /admin/reward-rules/{id} : Update a reward rule
     */
    @Override
    public ResponseEntity<RewardRuleResponse> adminRewardRulesIdPut(Long id, RewardRuleRequest rewardRuleRequest) {
        return rewardRuleService.adminRewardRulesIdPut(id, rewardRuleRequest);
    }

    //---------------------------------------------------------

    /**
     * POST /admin/reward-rules : Create a new reward rule
     */
    @Override
    public ResponseEntity<RewardRuleResponse> adminRewardRulesPost(RewardRuleRequest rewardRuleRequest) {
        return rewardRuleService.adminRewardRulesPost(rewardRuleRequest);
    }

    //---------------------------------------------------------

    /**
     * GET /admin/reward-rules/tier/{tierName} : Get reward rules by tier name
     */
    @Override
    public ResponseEntity<List<RewardRuleResponse>> adminRewardRulesTierTierNameGet(String tierName) {
        return rewardRuleService.adminRewardRulesTierTierNameGet(tierName);
    }
}
