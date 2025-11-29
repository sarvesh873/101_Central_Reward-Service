package com.central.reward_service.service;


import com.central.reward_service.kafka.RewardEventProducer;
import com.central.reward_service.model.Reward;
import com.central.reward_service.model.RewardRule;
import com.central.reward_service.repository.RewardRepository;
import com.central.reward_service.utils.ServiceUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.openapitools.model.RewardRequest;
import org.openapitools.model.RewardResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Autowired
    private ServiceUtils serviceUtils;
    @Autowired
    private RewardEventProducer rewardEventProducer;
    @Override
    @Transactional
    public ResponseEntity<RewardResponse>  processTransaction(RewardRequest request) {
        // 1. Idempotency Check
        if (rewardRepository.existsByTransactionId(request.getTransactionId())) {
            throw new IllegalStateException("Transaction already rewarded");
        }

        // 2. Get Rules from Caffeine Cache (Nano-second latency)
        TreeMap<Double, List<RewardRule>> cachedRules = serviceUtils.getCachedRewardRules();

        // 3. Determine the correct list of rewards for this amount
        List<RewardRule> applicableRules = determineApplicableRules(cachedRules, request.getTransactionAmount());

        // 4. Run the Weighted Algorithm
        RewardRule selectedRule = selectWeightedReward(applicableRules);

        // 5. Save & Return
        // Handle null rewardValue by defaulting to 0.0
        Double rewardValue = selectedRule.getRewardValue() != null ? selectedRule.getRewardValue() : 0.0;
        
        Reward reward = Reward.builder()
                .userId(request.getUserId())
                .transactionId(request.getTransactionId())
                .transactionAmount(request.getTransactionAmount())
                .rewardType(selectedRule.getRewardType())
                .rewardDescription(selectedRule.getDescription())
                .rewardValue(rewardValue)
                .rewardRule(selectedRule)
                .redeemCode("#@/*-+78(@)")
                .build();
        rewardRepository.save(reward);

        // 6. Send Reward Event to Kafka

        try{
            rewardEventProducer.sendRewardEvent(reward);
            log.info("Transaction event sent successfully with Transaction ID : {}", reward.getTransactionId());
        }
        catch (Exception e){
            log.error("Failed to create transaction: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(serviceUtils.constructRewardResponse(reward));
    }


//    ## Reward Retrieval Methods

    @Override
    @Cacheable(value = "user_rewards", key = "#rewardId")
    public ResponseEntity<RewardResponse>  getRewardById(Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new EntityNotFoundException("Reward not found: " + rewardId));
        return ResponseEntity.ok(serviceUtils.constructRewardResponse(reward));
    }

    @Override
    public ResponseEntity<List<RewardResponse>> getUserRewards(String userId, Integer page, Integer size) {
        Page<Reward> rewardPage = rewardRepository.findByUserIdOrderByCreatedAtDesc(
                userId, PageRequest.of(page, size));

        return ResponseEntity.ok(rewardPage.getContent().stream()
                .map(reward -> serviceUtils.constructRewardResponse(reward))
                .collect(Collectors.toList()));
    }


//    ## Private Helper Methods

    private List<RewardRule> determineApplicableRules(TreeMap<Double, List<RewardRule>> rules, Double amount) {
        log.info("Processing transaction amount: {}", amount);

        // Efficiently find the tier: floorEntry finds the closest key <= amount
        Map.Entry<Double, List<RewardRule>> entry = rules.floorEntry(amount);

        if (entry == null || entry.getValue().isEmpty()) {
            // No tier found, or the lowest tier is empty: throw config error
            throw new IllegalStateException("Transaction amount does not fall into any configured reward tier.");
        }
        return entry.getValue();
    }

    private RewardRule selectWeightedReward(List<RewardRule> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalStateException("Configuration Error: No rewards found for this tier");
        }

        // If there's only one option, return it immediately
        if (options.size() == 1) {
            return options.get(0);
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Calculate total weight
        int totalWeight = options.stream()
                .mapToInt(RewardRule::getWeight)
                .sum();

        if (totalWeight <= 0) {
            // If all weights are zero, treat them as equal probability
            return options.get(random.nextInt(options.size()));
        }

        // Generate a random value in the range [0, totalWeight)
        int randomValue = random.nextInt(totalWeight);
        int currentSum = 0;

        // Select a reward based on weights
        for (RewardRule rule : options) {
            currentSum += rule.getWeight();
            if (randomValue < currentSum) {
                log.debug("Selected reward: {} with weight {}", rule.getDescription(), rule.getWeight());
                return rule;
            }
        }

        // This should theoretically never be reached if the weights are positive
        return options.get(random.nextInt(options.size()));
    }

}