package com.central.reward_service.service;


import com.central.reward_service.exception.RewardClaimException;
import com.central.reward_service.exception.RewardNotFoundException;
import com.central.reward_service.kafka.RewardEventProducer;
import com.central.reward_service.model.Reward;
import com.central.reward_service.model.RewardRule;
import com.central.reward_service.model.RewardStatus;
import com.central.reward_service.repository.RewardRepository;
import com.central.reward_service.utils.ServiceUtils;
import com.central.reward_service.constants.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.openapitools.model.RewardRequest;
import org.openapitools.model.RewardResponse;
import org.openapitools.model.RewardClaimResponse;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
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
        log.info(Constants.LOG_REWARD_PROCESSING_START, request.getTransactionId());
        if (rewardRepository.existsByTransactionId(request.getTransactionId())) {
            throw new IllegalStateException(Constants.TRANSACTION_ALREADY_REWARDED);
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
                .redeemCode(String.valueOf(UUID.randomUUID()))
                .build();
        rewardRepository.save(reward);

        // 6. Send Reward Event to Kafka

        try{
            rewardEventProducer.sendRewardEvent(reward);
            log.info(Constants.LOG_REWARD_PROCESSED, request.getTransactionId());
        }
        catch (Exception e){
            log.error("Failed to create transaction: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(serviceUtils.constructRewardResponse(reward));
    }

    @Override
    @Transactional
    public ResponseEntity<RewardClaimResponse> claimReward(Long rewardId) {
        // 1. Fetch the reward
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RewardNotFoundException(Constants.REWARD_NOT_FOUND + rewardId));

        // 2. Check for expiry
        if (reward.getStatus() == RewardStatus.EXPIRED ||
                (reward.getExpiresAt() != null &&
                        reward.getExpiresAt().before(new Timestamp(System.currentTimeMillis())))) {
            reward.setStatus(RewardStatus.EXPIRED);
            rewardRepository.save(reward);
            log.warn(Constants.LOG_REWARD_EXPIRED, rewardId);
            throw new RewardClaimException(Constants.REWARD_ALREADY_CLAIMED);
        }

        // 3. Verify it's in UNCLAIMED state (not already claimed)
        if (reward.getStatus() != RewardStatus.UNCLAIMED) {
            throw new RewardClaimException(Constants.INVALID_REWARD_STATE + reward.getStatus());
        }

        // 4. Generate Redeem Code and Update Status
        String redeemCode = reward.getRedeemCode();
        if (redeemCode == null || redeemCode.isEmpty()) {
            redeemCode = String.valueOf(UUID.randomUUID());
            reward.setRedeemCode(redeemCode);
        }

        reward.setStatus(RewardStatus.CLAIMED);
        reward.setClaimedAt(new Timestamp(System.currentTimeMillis()));
        rewardRepository.save(reward);
        log.info(Constants.LOG_REWARD_CLAIMED, rewardId);

        // 5. Create and return the response
        RewardClaimResponse response = RewardClaimResponse.builder()
                .rewardStatus(RewardClaimResponse.RewardStatusEnum.CLAIMED)
                .expiresAt(reward.getExpiresAt().toInstant().atOffset(java.time.ZoneOffset.UTC))
                .redeemCode(redeemCode)
                .build();

        return ResponseEntity.ok(response);
    }


//    ## Reward Retrieval Methods

    @Override
    @Cacheable(value = "user_rewards", key = "#rewardId")
    public ResponseEntity<RewardResponse>  getRewardById(Long rewardId) {
        Reward reward = rewardRepository.findById(rewardId)
                .orElseThrow(() -> new RewardNotFoundException(Constants.REWARD_NOT_FOUND + rewardId));
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
        log.info(Constants.LOG_TRANSACTION_PROCESSING, amount);

        // Efficiently find the tier: floorEntry finds the closest key <= amount
        Map.Entry<Double, List<RewardRule>> entry = rules.floorEntry(amount);

        if (entry == null || entry.getValue().isEmpty()) {
            // No tier found, or the lowest tier is empty: throw config error
            throw new IllegalStateException(Constants.NO_REWARD_TIER);
        }
        return entry.getValue();
    }

    private RewardRule selectWeightedReward(List<RewardRule> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalStateException(Constants.NO_REWARDS_CONFIGURED);
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