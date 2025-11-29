package com.central.reward_service.utils;

import com.central.reward_service.model.Reward;
import com.central.reward_service.model.RewardRule;
import com.central.reward_service.repository.RewardRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.openapitools.model.RewardResponse;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceUtils {

    private final RewardRuleRepository ruleRepository;

    /**
     * This method is CACHED.
     * It hits the DB only once every hour (or configured time).
     * It transforms the flat DB list into a highly optimized TreeMap for fast lookup.
     */
    @Cacheable(value = "reward_rules", key = "'active_rules'")
    public TreeMap<Double, List<RewardRule>> getCachedRewardRules() {
        log.info("Cache Miss: Fetching Reward Rules from Database...");

        List<RewardRule> allRules = ruleRepository.findByActiveTrue();

        // Group by Min Transaction Amount to create Tiers dynamically
        Map<Double, List<RewardRule>> groupedRules = allRules.stream()
                .collect(Collectors.groupingBy(RewardRule::getMinTransactionAmount));

        // Convert to TreeMap for range lookups (floorEntry)
        return new TreeMap<>(groupedRules);
    }

    public RewardResponse constructRewardResponse(Reward reward){
        return RewardResponse.builder()
                .rewardId(reward.getRewardId())
                .transactionId(reward.getTransactionId())
                .rewardType(reward.getRewardType())
                .description(reward.getRewardDescription())
                .rewardValue(reward.getRewardValue())
                .userId(reward.getUserId())
                .createdAt(reward.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime())
                .build();
    }



}
