package com.central.reward_service.service;

import com.central.reward_service.exception.RewardNotFoundException;
import com.central.reward_service.model.RewardRule;
import com.central.reward_service.repository.RewardRuleRepository;
import com.central.reward_service.utils.ServiceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openapitools.model.RewardRuleRequest;
import org.openapitools.model.RewardRuleResponse;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RewardRuleServiceImplTest {

    @Mock
    private RewardRuleRepository rewardRuleRepository;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private RewardRuleServiceImpl rewardRuleService;

    private RewardRule testRule;
    private RewardRuleRequest testRequest;

    @BeforeEach
    void setUp() {
        testRule = new RewardRule();
        testRule.setId(1L);
        testRule.setTierName("GOLD");
        testRule.setMinTransactionAmount(1000.0);
        testRule.setRewardType("POINTS");
        testRule.setRewardValue(100.0);
        testRule.setWeight(1);
        testRule.setActive(true);
        testRule.setDescription("Test rule");

        testRequest = new RewardRuleRequest()
                .tierName("GOLD")
                .minTransactionAmount(1000.0)
                .rewardType("POINTS")
                .rewardValue(100.0)
                .weight(1)
                .active(true)
                .description("Test rule");
                
        // Set up common mock behavior
        when(cacheManager.getCache(anyString())).thenReturn(cache);
    }

    @Test
    void adminRewardRulesBulkPost_Success() {
        // Arrange
        List<RewardRuleRequest> requests = List.of(testRequest);
        when(rewardRuleRepository.saveAll(any())).thenReturn(List.of(testRule));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleService.adminRewardRulesBulkPost(requests);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(rewardRuleRepository).saveAll(any());
    }

    @Test
    void adminRewardRulesBulkPost_Exception() {
        // Arrange
        when(rewardRuleRepository.saveAll(any())).thenThrow(new RuntimeException("DB Error"));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleService.adminRewardRulesBulkPost(List.of(testRequest));

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void adminRewardRulesGet_Success() {
        // Arrange
        when(rewardRuleRepository.findAll()).thenReturn(List.of(testRule));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleService.adminRewardRulesGet();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void adminRewardRulesGet_Exception() {
        // Arrange
        when(rewardRuleRepository.findAll()).thenThrow(new RuntimeException("DB Error"));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleService.adminRewardRulesGet();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void adminRewardRulesIdDelete_Success() {
        // Arrange
        when(rewardRuleRepository.existsById(1L)).thenReturn(true);
        doNothing().when(rewardRuleRepository).deleteById(1L);

        // Act
        ResponseEntity<Void> response = rewardRuleService.adminRewardRulesIdDelete(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(rewardRuleRepository).deleteById(1L);
    }

    @Test
    void adminRewardRulesIdDelete_NotFound() {
        // Arrange
        when(rewardRuleRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(RewardNotFoundException.class, 
            () -> rewardRuleService.adminRewardRulesIdDelete(1L));
    }

    @Test
    void adminRewardRulesIdGet_Success() {
        // Arrange
        when(rewardRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));

        // Act
        ResponseEntity<RewardRuleResponse> response = 
            rewardRuleService.adminRewardRulesIdGet(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("GOLD", response.getBody().getTierName());
    }

    @Test
    void adminRewardRulesIdGet_NotFound() {
        // Arrange
        when(rewardRuleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RewardNotFoundException.class, 
            () -> rewardRuleService.adminRewardRulesIdGet(1L));
    }

    @Test
    void adminRewardRulesIdPut_Success() {
        // Arrange
        when(rewardRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));
        when(rewardRuleRepository.save(any())).thenReturn(testRule);

        // Act
        ResponseEntity<RewardRuleResponse> response = 
            rewardRuleService.adminRewardRulesIdPut(1L, testRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void adminRewardRulesIdPut_NotFound() {
        // Arrange
        when(rewardRuleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RewardNotFoundException.class, 
            () -> rewardRuleService.adminRewardRulesIdPut(1L, testRequest));
    }

    @Test
    void adminRewardRulesPost_Success() {
        // Arrange
        when(rewardRuleRepository.save(any())).thenReturn(testRule);
        when(cacheManager.getCache(anyString())).thenReturn(cache);

        // Act
        ResponseEntity<RewardRuleResponse> response = 
            rewardRuleService.adminRewardRulesPost(testRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("GOLD", response.getBody().getTierName());
    }

    @Test
    void adminRewardRulesPost_Exception() {
        // Arrange
        when(rewardRuleRepository.save(any())).thenThrow(new RuntimeException("DB Error"));

        // Act
        ResponseEntity<RewardRuleResponse> response = 
            rewardRuleService.adminRewardRulesPost(testRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void adminRewardRulesTierTierNameGet_Success() {
        // Arrange
        when(rewardRuleRepository.findByTierName("GOLD")).thenReturn(List.of(testRule));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleService.adminRewardRulesTierTierNameGet("GOLD");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("GOLD", response.getBody().get(0).getTierName());
    }

    @Test
    void adminRewardRulesTierTierNameGet_Exception() {
        // Arrange
        when(rewardRuleRepository.findByTierName(anyString()))
            .thenThrow(new RuntimeException("DB Error"));

        // Act
        ResponseEntity<List<RewardRuleResponse>> response = 
            rewardRuleService.adminRewardRulesTierTierNameGet("GOLD");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
