package com.central.reward_service.controller;

import com.central.reward_service.service.RewardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openapitools.model.RewardClaimResponse;
import org.openapitools.model.RewardRequest;
import org.openapitools.model.RewardResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class RewardControllerTest {

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private RewardController rewardController;

    private RewardResponse rewardResponse;
    private RewardRequest rewardRequest;
    private RewardClaimResponse claimResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize test data
        rewardResponse = new RewardResponse();
        rewardResponse.setRewardId(1L);
        rewardResponse.setUserId("user123");
        
        rewardRequest = new RewardRequest();
        rewardRequest.setUserId("user123");
        rewardRequest.setTransactionId("txn123");
        rewardRequest.setTransactionAmount(100.0);
        
        claimResponse = new RewardClaimResponse();
        claimResponse.setRewardStatus(RewardClaimResponse.RewardStatusEnum.CLAIMED);
    }

    @Test
    void getRewardById_ShouldReturnReward() {
        // Arrange
        when(rewardService.getRewardById(anyLong())).thenReturn(ResponseEntity.ok(rewardResponse));
        
        // Act
        ResponseEntity<RewardResponse> response = rewardController.getRewardById(1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rewardResponse, response.getBody());
        verify(rewardService, times(1)).getRewardById(1L);
    }

    @Test
    void getUserRewards_ShouldReturnUserRewards() {
        // Arrange
        List<RewardResponse> rewards = Arrays.asList(rewardResponse);
        when(rewardService.getUserRewards(anyString(), anyInt(), anyInt()))
            .thenReturn(ResponseEntity.ok(rewards));
        
        // Act
        ResponseEntity<List<RewardResponse>> response = 
            rewardController.getUserRewards("user123", 0, 10);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals(1, response.getBody().size());
        verify(rewardService, times(1)).getUserRewards("user123", 0, 10);
    }

    @Test
    void processTransaction_ShouldProcessSuccessfully() {
        // Arrange
        when(rewardService.processTransaction(any(RewardRequest.class)))
            .thenReturn(ResponseEntity.ok(rewardResponse));
        
        // Act
        ResponseEntity<RewardResponse> response = 
            rewardController.processTransaction(rewardRequest);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rewardResponse, response.getBody());
        verify(rewardService, times(1)).processTransaction(rewardRequest);
    }

    @Test
    void claimReward_ShouldClaimSuccessfully() {
        // Arrange
        when(rewardService.claimReward(anyLong()))
            .thenReturn(ResponseEntity.ok(claimResponse));
        
        // Act
        ResponseEntity<RewardClaimResponse> response = 
            rewardController.claimReward(1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(claimResponse, response.getBody());
        verify(rewardService, times(1)).claimReward(1L);
    }

    @Test
    void processTransaction_WhenServiceThrowsException_ShouldPropagateException() {
        // Arrange
        when(rewardService.processTransaction(any(RewardRequest.class)))
            .thenThrow(new RuntimeException("Service error"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            rewardController.processTransaction(rewardRequest));
    }

    @Test
    void claimReward_WhenRewardNotFound_ShouldPropagateException() {
        // Arrange
        when(rewardService.claimReward(anyLong()))
            .thenThrow(new RuntimeException("Reward not found"));
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            rewardController.claimReward(999L));
    }
}
