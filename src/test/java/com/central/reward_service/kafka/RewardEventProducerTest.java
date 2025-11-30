package com.central.reward_service.kafka;

import com.central.reward_service.model.Reward;
import com.central.reward_service.model.RewardStatus;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import java.util.concurrent.CompletableFuture;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardEventProducerTest {

    @Mock
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @InjectMocks
    private RewardEventProducer rewardEventProducer;

    @Captor
    private ArgumentCaptor<String> topicCaptor;
    @Captor
    private ArgumentCaptor<String> keyCaptor;
    @Captor
    private ArgumentCaptor<byte[]> valueCaptor;

    private Reward reward;
    private CompletableFuture<SendResult<String, byte[]>> future;

    private Reward createTestReward() {
        return Reward.builder()
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
    }
    
    @BeforeEach
    void setUp() {
        reward = createTestReward();
        future = new CompletableFuture<>();
        // Set up the topic value for testing using the correct field name
        ReflectionTestUtils.setField(rewardEventProducer, "REWARD_TOPIC", "reward-generated-events");
    }

    @Test
    void sendRewardEvent_ShouldSendToKafka() throws ExecutionException, InterruptedException {
        // Arrange
        when(kafkaTemplate.send(anyString(), anyString(), any(byte[].class))).thenReturn(future);
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition("reward-generated-events", 0), 1L, 0, 0L, 0, 0);
        future.complete(new SendResult<>(null, metadata));

        // Act
        rewardEventProducer.sendRewardEvent(reward);

        // Assert
        verify(kafkaTemplate, times(1)).send(
            topicCaptor.capture(), 
            keyCaptor.capture(), 
            valueCaptor.capture()
        );
        
        assertEquals("reward-generated-events", topicCaptor.getValue());
        assertEquals(reward.getUserId(), keyCaptor.getValue());
    }

    @Test
    void sendRewardEvent_WhenKafkaFails_ShouldLogError() {
        // Arrange
        CompletableFuture<SendResult<String, byte[]>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka error"));
        when(kafkaTemplate.send(anyString(), anyString(), any(byte[].class)))
                .thenReturn(failedFuture);

        // Act
        rewardEventProducer.sendRewardEvent(reward);
    }

    @Test
    void sendRewardEvent_WithNullReward_ShouldNotSend() {
        // Reset the mock to clear any setup from @BeforeEach
        reset(kafkaTemplate);
        
        // Act
        rewardEventProducer.sendRewardEvent(null);

        // Assert
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any(byte[].class));
    }
}
