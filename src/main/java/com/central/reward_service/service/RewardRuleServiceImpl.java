package com.central.reward_service.service;

import com.central.reward_service.constants.Constants;
import com.central.reward_service.exception.RewardNotFoundException;
import com.central.reward_service.model.RewardRule;
import com.central.reward_service.repository.RewardRuleRepository;
import com.central.reward_service.utils.ServiceUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.model.RewardRuleRequest;
import org.openapitools.model.RewardRuleResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardRuleServiceImpl implements RewardRuleService {

    private final RewardRuleRepository rewardRuleRepository;

    @Override
    @Transactional
    public ResponseEntity<List<RewardRuleResponse>> adminRewardRulesBulkPost(List<RewardRuleRequest> rewardRuleRequests) {
        try {
            List<RewardRule> rules = rewardRuleRequests.stream()
                    .map(ServiceUtils::constructRewardRuleFromRequest)
                    .collect(Collectors.toList());

            List<RewardRule> savedRules = rewardRuleRepository.saveAll(rules);
            log.info("Bulk created {} reward rules", savedRules.size());

            List<RewardRuleResponse> response = savedRules.stream()
                    .map(ServiceUtils::constructRewardRuleResponse)
                    .collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating reward rules in bulk: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<RewardRuleResponse>> adminRewardRulesGet() {
        try {
            List<RewardRuleResponse> response = rewardRuleRepository.findAll().stream()
                    .map(ServiceUtils::constructRewardRuleResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving all reward rules: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Void> adminRewardRulesIdDelete(Long id) {
        try {
            if (!rewardRuleRepository.existsById(id)) {
                throw new RewardNotFoundException("Reward rule not found with id: " + id);
            }
            rewardRuleRepository.deleteById(id);
            log.info("Deleted reward rule with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (RewardNotFoundException e) {
            log.warn("Attempted to delete non-existent reward rule with id: {}", id);
            throw new RewardNotFoundException(Constants.REWARD_NOT_FOUND + id);
        } catch (Exception e) {
            log.error("Error deleting reward rule with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<RewardRuleResponse> adminRewardRulesIdGet(Long id) {
        try {
            RewardRule rule = rewardRuleRepository.findById(id)
                    .orElseThrow(() -> new RewardNotFoundException("Reward rule not found with id: " + id));
            return ResponseEntity.ok(ServiceUtils.constructRewardRuleResponse(rule));
        } catch (RewardNotFoundException e) {
            log.warn("Reward rule not found with id: {}", id);
            throw new RewardNotFoundException(Constants.REWARD_NOT_FOUND + id);
        } catch (Exception e) {
            log.error("Error retrieving reward rule with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<RewardRuleResponse> adminRewardRulesIdPut(Long id, RewardRuleRequest rewardRuleRequest) {
        try {
            return rewardRuleRepository.findById(id)
                    .map(rule -> {
                        RewardRule updatedRule = ServiceUtils.updateRewardRuleFromRequest(rule, rewardRuleRequest);
                        RewardRule savedRule = rewardRuleRepository.save(updatedRule);
                        log.info("Updated reward rule with id: {}", id);
                        return ResponseEntity.ok(ServiceUtils.constructRewardRuleResponse(savedRule));
                    })
                    .orElseThrow(() -> new RewardNotFoundException("Reward rule not found with id: " + id));
        } catch (RewardNotFoundException e) {
            log.warn("Attempted to update non-existent reward rule with id: {}", id);
            throw new RewardNotFoundException(Constants.REWARD_NOT_FOUND + id);
        } catch (Exception e) {
            log.error("Error updating reward rule with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<RewardRuleResponse> adminRewardRulesPost(RewardRuleRequest rewardRuleRequest) {
        try {
            RewardRule rule = ServiceUtils.constructRewardRuleFromRequest(rewardRuleRequest);
            RewardRule savedRule = rewardRuleRepository.save(rule);
            log.info("Created new reward rule with id: {}", savedRule.getId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ServiceUtils.constructRewardRuleResponse(savedRule));
        } catch (Exception e) {
            log.error("Error creating reward rule: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<RewardRuleResponse>> adminRewardRulesTierTierNameGet(String tierName) {
        try {
            List<RewardRuleResponse> response = rewardRuleRepository.findByTierName(tierName).stream()
                    .map(ServiceUtils::constructRewardRuleResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving reward rules for tier {}: {}", tierName, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}