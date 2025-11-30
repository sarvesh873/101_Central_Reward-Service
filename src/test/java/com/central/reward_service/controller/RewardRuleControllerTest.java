package com.central.reward_service.controller;

import com.central.reward_service.service.RewardRuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.RewardRuleRequest;
import org.openapitools.model.RewardRuleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardRuleControllerTest {

    @Mock
    private RewardRuleService rewardRuleService;

    @InjectMocks
    private RewardRuleController rewardRuleController;

    private RewardRuleResponse testResponse;
    private RewardRuleRequest testRequest;

    @BeforeEach
    void setUp() {
        testResponse = new RewardRuleResponse()
                .id(1L)
                .tierName("GOLD")
                .minTransactionAmount(1000.0)
                .rewardType("POINTS")
                .rewardValue(100.0)
                .weight(1)
                .active(true)
                .description("Test rule");

        testRequest = new RewardRuleRequest()
                .tierName("GOLD")
                .minTransactionAmount(1000.0)
                .rewardType("POINTS")
                .rewardValue(100.0)
                .weight(1)
                .active(true)
                .description("Test rule");
    }

    @Test
    void adminRewardRulesBulkPost_ShouldCallService() {
        // Arrange
        when(rewardRuleService.adminRewardRulesBulkPost(any()))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(List.of(testResponse)));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleController.adminRewardRulesBulkPost(List.of(testRequest));

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rewardRuleService).adminRewardRulesBulkPost(any());
    }

    @Test
    void adminRewardRulesGet_ShouldCallService() {
        // Arrange
        when(rewardRuleService.adminRewardRulesGet())
            .thenReturn(ResponseEntity.ok(List.of(testResponse)));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleController.adminRewardRulesGet();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rewardRuleService).adminRewardRulesGet();
    }

    @Test
    void adminRewardRulesIdDelete_ShouldCallService() {
        // Arrange
        when(rewardRuleService.adminRewardRulesIdDelete(1L))
            .thenReturn(ResponseEntity.noContent().build());

        // Act
        ResponseEntity<Void> response = rewardRuleController.adminRewardRulesIdDelete(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rewardRuleService).adminRewardRulesIdDelete(1L);
    }

    @Test
    void adminRewardRulesIdGet_ShouldCallService() {
        // Arrange
        when(rewardRuleService.adminRewardRulesIdGet(1L))
            .thenReturn(ResponseEntity.ok(testResponse));

        // Act
        ResponseEntity<RewardRuleResponse> response = 
            rewardRuleController.adminRewardRulesIdGet(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("GOLD", response.getBody().getTierName());
        verify(rewardRuleService).adminRewardRulesIdGet(1L);
    }

    @Test
    void adminRewardRulesIdPut_ShouldCallService() {
        // Arrange
        when(rewardRuleService.adminRewardRulesIdPut(eq(1L), any(RewardRuleRequest.class)))
            .thenReturn(ResponseEntity.ok(testResponse));

        // Act
        ResponseEntity<RewardRuleResponse> response = 
            rewardRuleController.adminRewardRulesIdPut(1L, testRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(rewardRuleService).adminRewardRulesIdPut(eq(1L), any(RewardRuleRequest.class));
    }

    @Test
    void adminRewardRulesPost_ShouldCallService() {
        // Arrange
        when(rewardRuleService.adminRewardRulesPost(any(RewardRuleRequest.class)))
            .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(testResponse));

        // Act
        ResponseEntity<RewardRuleResponse> response = 
            rewardRuleController.adminRewardRulesPost(testRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("GOLD", response.getBody().getTierName());
        verify(rewardRuleService).adminRewardRulesPost(any(RewardRuleRequest.class));
    }

    @Test
    void adminRewardRulesTierTierNameGet_ShouldCallService() {
        // Arrange
        when(rewardRuleService.adminRewardRulesTierTierNameGet("GOLD"))
            .thenReturn(ResponseEntity.ok(List.of(testResponse)));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleController.adminRewardRulesTierTierNameGet("GOLD");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("GOLD", response.getBody().get(0).getTierName());
        verify(rewardRuleService).adminRewardRulesTierTierNameGet("GOLD");
    }
}
