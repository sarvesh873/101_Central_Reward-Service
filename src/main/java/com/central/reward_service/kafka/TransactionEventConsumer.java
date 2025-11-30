package com.central.reward_service.kafka;

import com.central.reward_service.service.RewardService;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reward.events.TransactionEvent;
import org.openapitools.model.RewardRequest;


@Slf4j
@Component
public class TransactionEventConsumer {

    private static final String SENDER_TOPIC = "${kafka.topics.reward_service.receiver}";

    private static final String REWARD_SERVICE_GROUP_ID = "${spring.kafka.consumer.group-id}";

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    private final RewardService rewardService;


    @Autowired
    public TransactionEventConsumer(KafkaTemplate<String,  byte[]> kafkaTemplate, RewardService rewardService) {
        this.kafkaTemplate = kafkaTemplate;
        this.rewardService = rewardService;
    }

    @KafkaListener(topics = SENDER_TOPIC, groupId = REWARD_SERVICE_GROUP_ID)
    public void handleSenderTransaction(byte[] event) {
        long startTime = System.currentTimeMillis();
        String transactionId = "";

        try {
            log.info("Received transaction event from Kafka - Starting reward processing");

            // Parse the protobuf event
            TransactionEvent transactionEvent = TransactionEvent.parseFrom(event);
            transactionId = transactionEvent.getTransactionId();
            String userId = transactionEvent.getSenderId();
            double amount = transactionEvent.getAmount();
            
            log.info("Processing reward for transaction - ID: {}, User: {}, Amount: {}",
                    transactionId, userId, amount);

            // Process and save reward
            long processStart = System.currentTimeMillis();
            RewardRequest request = RewardRequest.builder()
                    .transactionId(transactionId)
                    .transactionAmount(amount)
                    .userId(userId)
                    .build();
                    
            log.debug("Dispatching reward processing request - Transaction: {}", transactionId);
            rewardService.processTransaction(request);
            
            long processingTime = System.currentTimeMillis() - processStart;
            log.info("Successfully processed reward - Transaction: {}, Processing Time: {}ms",
                    transactionId, processingTime);

        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse transaction event after {} ms. Error: {}",
                    (System.currentTimeMillis() - startTime), e.getMessage(), e);
            throw new RuntimeException("Failed to process transaction event", e);
        } finally {
            log.info("Completed processing for transaction: {} - Total time taken: {} ms",
                    transactionId, (System.currentTimeMillis() - startTime));
        }
    }

}
