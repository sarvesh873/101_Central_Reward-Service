package com.central.reward_service.service;

import com.central.reward_service.exception.RewardClaimException;
import com.central.reward_service.exception.RewardNotFoundException;
import com.central.reward_service.kafka.RewardEventProducer;
import com.central.reward_service.model.Reward;
import com.central.reward_service.model.RewardRule;
import com.central.reward_service.model.RewardStatus;
import com.central.reward_service.repository.RewardRepository;
import com.central.reward_service.utils.ServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.RewardClaimResponse;
import org.openapitools.model.RewardRequest;
import org.openapitools.model.RewardResponse;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardServiceImplTest {

    @Mock
    private RewardRepository rewardRepository;

    @Mock
    private ServiceUtils serviceUtils;

    @Mock
    private RewardEventProducer rewardEventProducer;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private RewardServiceImpl rewardService;

    private RewardRequest rewardRequest;
    private Reward reward;
    private RewardRule rewardRule;
    private TreeMap<Double, List<RewardRule>> rewardRules;

    @BeforeEach
    void setUp() {
        // Initialize test data
        rewardRequest = new RewardRequest();
        rewardRequest.setUserId("user123");
        rewardRequest.setTransactionId("txn123");
        rewardRequest.setTransactionAmount(100.0);

        rewardRule = new RewardRule();
        rewardRule.setId(1L);
        rewardRule.setMinTransactionAmount(50.0);
        rewardRule.setRewardType("CASHBACK");
        rewardRule.setDescription("5% cashback");
        rewardRule.setWeight(5);
        rewardRule.setRewardValue(5.0);

        reward = Reward.builder()
                .rewardId(1L)
                .userId("user123")
                .transactionId("txn123")
                .transactionAmount(100.0)
                .rewardType("CASHBACK")
                .rewardDescription("5% cashback")
                .rewardValue(5.0)
                .status(RewardStatus.UNCLAIMED)
                .redeemCode(UUID.randomUUID().toString())
                .expiresAt(Timestamp.from(Instant.now().plusSeconds(86400)))
                .build();

        rewardRules = new TreeMap<>();
        rewardRules.put(50.0, Arrays.asList(rewardRule));
    }

    @Test
    void processTransaction_WhenNewTransaction_ShouldProcessSuccessfully() {
        // Arrange
        when(rewardRepository.existsByTransactionId(anyString())).thenReturn(false);
        when(serviceUtils.getCachedRewardRules()).thenReturn(rewardRules);
        when(rewardRepository.save(any(Reward.class))).thenReturn(reward);
        when(serviceUtils.constructRewardResponse(any(Reward.class))).thenReturn(new RewardResponse());

        // Act
        ResponseEntity<RewardResponse> response = rewardService.processTransaction(rewardRequest);

        // Assert
        assertNotNull(response);
        verify(rewardRepository, times(1)).save(any(Reward.class));
        verify(rewardEventProducer, times(1)).sendRewardEvent(any(Reward.class));
    }

    @Test
    void processTransaction_WhenDuplicateTransaction_ShouldThrowException() {
        // Arrange
        when(rewardRepository.existsByTransactionId(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> 
            rewardService.processTransaction(rewardRequest));
    }

    @Test
    void claimReward_WhenValid_ShouldClaimSuccessfully() {
        // Arrange
        reward.setStatus(RewardStatus.UNCLAIMED);
        when(rewardRepository.findById(anyLong())).thenReturn(Optional.of(reward));
        when(rewardRepository.save(any(Reward.class))).thenReturn(reward);

        // Act
        ResponseEntity<RewardClaimResponse> response = rewardService.claimReward(1L);

        // Assert
        assertNotNull(response);
        assertEquals(RewardStatus.CLAIMED, reward.getStatus());
        assertNotNull(reward.getClaimedAt());
    }

    @Test
    void claimReward_WhenAlreadyClaimed_ShouldThrowException() {
        // Arrange
        reward.setStatus(RewardStatus.CLAIMED);
        when(rewardRepository.findById(anyLong())).thenReturn(Optional.of(reward));

        // Act & Assert
        assertThrows(RewardClaimException.class, () -> 
            rewardService.claimReward(1L));
    }

    @Test
    void claimReward_WhenExpired_ShouldThrowException() {
        // Arrange
        reward.setStatus(RewardStatus.UNCLAIMED);
        reward.setExpiresAt(Timestamp.from(Instant.now().minusSeconds(3600)));
        when(rewardRepository.findById(anyLong())).thenReturn(Optional.of(reward));

        // Act & Assert
        assertThrows(RewardClaimException.class, () -> 
            rewardService.claimReward(1L));
        assertEquals(RewardStatus.EXPIRED, reward.getStatus());
    }

    @Test
    void getRewardById_WhenExists_ShouldReturnReward() {
        // Arrange
        when(rewardRepository.findById(anyLong())).thenReturn(Optional.of(reward));
        when(serviceUtils.constructRewardResponse(any(Reward.class))).thenReturn(new RewardResponse());

        // Act
        ResponseEntity<RewardResponse> response = rewardService.getRewardById(1L);

        // Assert
        assertNotNull(response);
        verify(rewardRepository, times(1)).findById(1L);
    }

    @Test
    void getRewardById_WhenNotExists_ShouldThrowException() {
        // Arrange
        when(rewardRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RewardNotFoundException.class, () -> 
            rewardService.getRewardById(999L));
    }

    @Test
    void getUserRewards_ShouldReturnPaginatedResults() {
        // Arrange
        Page<Reward> page = new PageImpl<>(Arrays.asList(reward));
        when(rewardRepository.findByUserIdOrderByCreatedAtDesc(anyString(), any(PageRequest.class)))
            .thenReturn(page);
        when(serviceUtils.constructRewardResponse(any(Reward.class))).thenReturn(new RewardResponse());

        // Act
        ResponseEntity<List<RewardResponse>> response = 
            rewardService.getUserRewards("user123", 0, 10);

        // Assert
        assertNotNull(response);
        assertFalse(response.getBody().isEmpty());
        verify(rewardRepository, times(1))
            .findByUserIdOrderByCreatedAtDesc(eq("user123"), any(PageRequest.class));
    }

//    @Test
//    void determineApplicableRules_WhenValidAmount_ShouldReturnRules() {
//        // Act
//        List<RewardRule> result = rewardService.determineApplicableRules(rewardRules, 100.0);
//
//        // Assert
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//        assertEquals(1, result.size());
//        assertEquals(rewardRule, result.get(0));
//    }
//
//    @Test
//    void determineApplicableRules_WhenNoMatchingTier_ShouldThrowException() {
//        // Arrange
//        TreeMap<Double, List<RewardRule>> emptyRules = new TreeMap<>();
//
//        // Act & Assert
//        assertThrows(IllegalStateException.class, () ->
//            rewardService.determineApplicableRules(emptyRules, 10.0));
//    }
//
//    @Test
//    void selectWeightedReward_WhenSingleOption_ShouldReturnIt() {
//        // Act
//        RewardRule result = rewardService.selectWeightedReward(Arrays.asList(rewardRule));
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(rewardRule, result);
//    }
//
//    @Test
//    void selectWeightedReward_WhenMultipleOptions_ShouldReturnBasedOnWeight() {
//        // Arrange
//        RewardRule rule1 = new RewardRule();
//        rule1.setWeight(1);
//        RewardRule rule2 = new RewardRule();
//        rule2.setWeight(9); // 90% chance
//
//        // Act
//        // Run multiple times to check distribution (statistical test)
//        Map<RewardRule, Integer> counts = new HashMap<>();
//        int totalRuns = 1000;
//
//        for (int i = 0; i < totalRuns; i++) {
//            RewardRule selected = rewardService.selectWeightedReward(Arrays.asList(rule1, rule2));
//            counts.put(selected, counts.getOrDefault(selected, 0) + 1);
//        }
//
//        // Assert
//        assertTrue(counts.get(rule2) > counts.get(rule1));
//    }
}
