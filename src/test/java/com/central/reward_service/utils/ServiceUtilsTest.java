package com.central.reward_service.utils;

import com.central.reward_service.model.Reward;
import com.central.reward_service.model.RewardRule;
import com.central.reward_service.repository.RewardRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.RewardResponse;
import org.openapitools.model.RewardRuleRequest;
import org.openapitools.model.RewardRuleResponse;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceUtilsTest {

    @Mock
    private RewardRuleRepository ruleRepository;

    @InjectMocks
    private ServiceUtils serviceUtils;

    private RewardRule activeRule1;
    private RewardRule activeRule2;
    private RewardRule inactiveRule;
    private Reward reward;

    @BeforeEach
    void setUp() {
        activeRule1 = RewardRule.builder()
                .id(1L)
                .tierName("GOLD")
                .minTransactionAmount(1000.0)
                .rewardType("POINTS")
                .rewardValue(100.0)
                .weight(1)
                .active(true)
                .description("Gold tier reward")
                .build();

        activeRule2 = RewardRule.builder()
                .id(2L)
                .tierName("PLATINUM")
                .minTransactionAmount(5000.0)
                .rewardType("CASHBACK")
                .rewardValue(200.0)
                .weight(2)
                .active(true)
                .description("Platinum tier reward")
                .build();

        inactiveRule = RewardRule.builder()
                .id(3L)
                .tierName("SILVER")
                .minTransactionAmount(100.0)
                .rewardType("POINTS")
                .rewardValue(50.0)
                .weight(0)
                .active(false)
                .description("Inactive silver tier")
                .build();

        reward = Reward.builder()
                .rewardId(1L)
                .transactionId("TXN123")
                .userId("USER123")
                .rewardType("POINTS")
                .rewardDescription("Test reward")
                .rewardValue(100.0)
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }

    @Test
    void getCachedRewardRules_ShouldReturnActiveRulesAsTreeMap() {
        // Arrange
        // Create two rules with the same minTransactionAmount to test grouping
        RewardRule anotherGoldRule = RewardRule.builder()
                .id(3L)
                .tierName("GOLD_EXTRA")
                .minTransactionAmount(1000.0)
                .rewardType("CASHBACK")
                .rewardValue(150.0)
                .weight(1)
                .active(true)
                .description("Additional gold rule")
                .build();
                
        List<RewardRule> activeRules = Arrays.asList(activeRule1, activeRule2, anotherGoldRule);
        when(ruleRepository.findByActiveTrue()).thenReturn(activeRules);

        // Act
        var result = serviceUtils.getCachedRewardRules();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size()); // Should have 2 groups (1000.0 and 5000.0)
        assertTrue(result.containsKey(1000.0));
        assertTrue(result.containsKey(5000.0));
        assertEquals(2, result.get(1000.0).size()); // Should have 2 rules for 1000.0
        assertEquals(1, result.get(5000.0).size()); // Should have 1 rule for 5000.0
    }

    @Test
    void getCachedRewardRules_WithNoActiveRules_ShouldReturnEmptyMap() {
        // Arrange
        when(ruleRepository.findByActiveTrue()).thenReturn(List.of());

        // Act
        var result = serviceUtils.getCachedRewardRules();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void constructRewardResponse_ShouldMapAllFieldsCorrectly() {
        // Act
        RewardResponse response = serviceUtils.constructRewardResponse(reward);

        // Assert
        assertNotNull(response);
        assertEquals(reward.getRewardId(), response.getRewardId());
        assertEquals(reward.getTransactionId(), response.getTransactionId());
        assertEquals(reward.getUserId(), response.getUserId());
        assertEquals(reward.getRewardType(), response.getRewardType());
        assertEquals(reward.getRewardDescription(), response.getDescription());
        assertEquals(reward.getRewardValue(), response.getRewardValue());
        assertNotNull(response.getCreatedAt());
    }

    @Test
    void constructRewardResponse_WithNullReward_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> serviceUtils.constructRewardResponse(null));
    }

    @Test
    void constructRewardRuleResponse_ShouldMapAllFieldsCorrectly() {
        // Act
        RewardRuleResponse response = ServiceUtils.constructRewardRuleResponse(activeRule1);

        // Assert
        assertNotNull(response);
        assertEquals(activeRule1.getId(), response.getId());
        assertEquals(activeRule1.getMinTransactionAmount(), response.getMinTransactionAmount());
        assertEquals(activeRule1.isActive(), response.getActive());
        assertEquals(activeRule1.getTierName(), response.getTierName());
        assertEquals(activeRule1.getWeight(), response.getWeight());
        assertEquals(activeRule1.getDescription(), response.getDescription());
        assertEquals(activeRule1.getRewardType(), response.getRewardType());
        assertEquals(activeRule1.getRewardValue(), response.getRewardValue());
    }

    @Test
    void constructRewardRuleResponse_WithNullRule_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> ServiceUtils.constructRewardRuleResponse(null));
    }

    @Test
    void constructRewardRuleFromRequest_ShouldMapAllFieldsCorrectly() {
        // Arrange
        RewardRuleRequest request = new RewardRuleRequest()
                .tierName("GOLD")
                .minTransactionAmount(1000.0)
                .rewardType("POINTS")
                .description("Test rule")
                .rewardValue(100.0)
                .weight(1)
                .active(true);

        // Act
        RewardRule rule = ServiceUtils.constructRewardRuleFromRequest(request);

        // Assert
        assertNotNull(rule);
        assertEquals(request.getTierName(), rule.getTierName());
        assertEquals(request.getMinTransactionAmount(), rule.getMinTransactionAmount());
        assertEquals(request.getRewardType(), rule.getRewardType());
        assertEquals(request.getDescription(), rule.getDescription());
        assertEquals(request.getRewardValue(), rule.getRewardValue());
        assertEquals(request.getWeight(), rule.getWeight());
        assertEquals(request.getActive(), rule.isActive());
    }

    @Test
    void constructRewardRuleFromRequest_WithNullRequest_ShouldThrowException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> ServiceUtils.constructRewardRuleFromRequest(null));
    }

    @Test
    void updateRewardRuleFromRequest_ShouldUpdateAllFields() {
        // Arrange
        RewardRule rule = new RewardRule();
        RewardRuleRequest request = new RewardRuleRequest()
                .tierName("UPDATED")
                .minTransactionAmount(2000.0)
                .rewardType("CASHBACK")
                .description("Updated description")
                .rewardValue(200.0)
                .weight(2)
                .active(false);

        // Act
        RewardRule updatedRule = ServiceUtils.updateRewardRuleFromRequest(rule, request);

        // Assert
        assertSame(rule, updatedRule);
        assertEquals(request.getTierName(), updatedRule.getTierName());
        assertEquals(request.getMinTransactionAmount(), updatedRule.getMinTransactionAmount());
        assertEquals(request.getRewardType(), updatedRule.getRewardType());
        assertEquals(request.getDescription(), updatedRule.getDescription());
        assertEquals(request.getRewardValue(), updatedRule.getRewardValue());
        assertEquals(request.getWeight(), updatedRule.getWeight());
        assertEquals(request.getActive(), updatedRule.isActive());
    }

    @Test
    void updateRewardRuleFromRequest_WithNullRule_ShouldThrowException() {
        // Arrange
        RewardRuleRequest request = new RewardRuleRequest();

        // Act & Assert
        assertThrows(NullPointerException.class, 
            () -> ServiceUtils.updateRewardRuleFromRequest(null, request));
    }

    @Test
    void updateRewardRuleFromRequest_WithNullRequest_ShouldThrowException() {
        // Arrange
        RewardRule rule = new RewardRule();

        // Act & Assert
        assertThrows(NullPointerException.class, 
            () -> ServiceUtils.updateRewardRuleFromRequest(rule, null));
    }
}
