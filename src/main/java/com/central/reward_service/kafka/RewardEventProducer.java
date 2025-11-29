package com.central.reward_service.kafka;

import com.central.reward_service.model.Reward;
import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import reward.events.RewardEvent;
import reward.events.RewardType;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class RewardEventProducer {
    private static final String REWARD_TOPIC = "reward-generated-events";

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Autowired
    public RewardEventProducer(KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a reward event to Kafka
     * @param reward The reward details from the Reward model
     */
    public void sendRewardEvent(Reward reward) {
        if (reward == null) {
            log.error("Cannot send null reward event");
            return;
        }

        log.info("Sending reward event for user: {}, transaction: {}", 
                reward.getUserId(), reward.getTransactionId());

        try {
            // Build the reward event
            RewardEvent rewardEvent = RewardEvent.newBuilder()
                    .setRewardId(String.valueOf(reward.getRewardId()))
                    .setTransactionId(reward.getTransactionId())
                    .setUserId(reward.getUserId())
                    .setRewardType(RewardType.valueOf(reward.getRewardType()))
                    .setRewardValue(reward.getRewardValue())
                    .setRewardDescription(reward.getRewardDescription())
                    .setTransactionAmount(reward.getTransactionAmount())
                    .setCreatedAt(convertToTimestamp(reward.getCreatedAt()))
                    .setNotificationMessage(reward.getRewardDescription())
                    .build();

            // Send the event to Kafka
            CompletableFuture<SendResult<String, byte[]>> future = 
                    kafkaTemplate.send(REWARD_TOPIC, reward.getUserId(), rewardEvent.toByteArray());

            future.thenAccept(result -> {
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("Reward event sent successfully! User: {}, Reward Type: {}, Topic: {}, Partition: {}",
                        reward.getUserId(), reward.getRewardType(), metadata.topic(), metadata.partition());
            }).exceptionally(ex -> {
                log.error("Failed to send reward event for user {}: {}", 
                        reward.getUserId(), ex.getMessage(), ex);
                return null;
            });

        } catch (Exception e) {
            log.error("Error building or sending reward event: {}", e.getMessage(), e);
        }
    }
    /**
     * Convert java.sql.Timestamp to protobuf Timestamp
     */
    private Timestamp convertToTimestamp(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return Timestamp.newBuilder().setSeconds(Instant.now().getEpochSecond()).build();
        }
        return Timestamp.newBuilder()
                .setSeconds(timestamp.toInstant().getEpochSecond())
                .setNanos(timestamp.getNanos())
                .build();
    }


}
