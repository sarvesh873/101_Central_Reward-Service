//package com.central.transaction_service.kafka;
//
//import com.google.protobuf.InvalidProtocolBufferException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//import reward.events.TransactionEvent;
//
//@Slf4j
//@Component
//public class TransactionEventConsumer {
//    private static final String SENDER_TOPIC = "txn-sender-events";
//
//    private final KafkaTemplate<String, byte[]> kafkaTemplate;
//
//
//    @Autowired
//    public KafkaNotificationsConsumer(KafkaTemplate<String,  byte[]> kafkaTemplate, NotificationService notificationService) {
//        this.kafkaTemplate = kafkaTemplate;
//        this.notificationService = notificationService;
//    }
//
//    @KafkaListener(topics = SENDER_TOPIC, groupId = "notification-service")
//    public void handleSenderTransaction(byte[] event) {
//        long startTime = System.currentTimeMillis();
//        String transactionId = "";
//
//        try {
//            log.info("Successfully received {} notification for transaction: {} - Took {} ms", transactionId, System.currentTimeMillis());
//
//            TransactionEvent transactionEvent = TransactionEvent.parseFrom(event);
//            transactionId = transactionEvent.getTransactionId();
//            log.info("Processing {} event - Transaction ID: {} - Event data: {}", transactionId, transactionEvent);
//
//            // Process and save notification
//            long processStart = System.currentTimeMillis();
//            Notification notification = createNotificationFromEvent(transactionEvent, eventType);
//            notificationService.saveNotification(notification);
//            log.info("Successfully processed and saved {} notification for transaction: {} - Took {} ms",
//                    eventType.toLowerCase(), transactionId, (System.currentTimeMillis() - processStart));
//
//        } catch (InvalidProtocolBufferException e) {
//            log.error("Failed to parse transaction event after {} ms. Error: {}",
//                    (System.currentTimeMillis() - startTime), e.getMessage(), e);
//            throw new RuntimeException("Failed to process transaction event", e);
//        } finally {
//            log.info("Completed processing for transaction: {} - Total time taken: {} ms",
//                    transactionId, (System.currentTimeMillis() - startTime));
//        }
//    }
//
//}
