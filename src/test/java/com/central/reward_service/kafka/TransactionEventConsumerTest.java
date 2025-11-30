package com.central.reward_service.kafka;

import com.central.reward_service.service.RewardService;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.model.RewardRequest;
import org.springframework.kafka.core.KafkaTemplate;
import reward.events.TransactionEvent;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionEventConsumerTest {

    @Mock
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @Mock
    private RewardService rewardService;

    @InjectMocks
    private TransactionEventConsumer transactionEventConsumer;

    @Captor
    private ArgumentCaptor<RewardRequest> rewardRequestCaptor;

    private TransactionEvent transactionEvent;
    private byte[] serializedEvent;

    @BeforeEach
    void setUp() throws Exception {
        // Create a test transaction event using the builder pattern
        transactionEvent = TransactionEvent.newBuilder()
                .setTransactionId("txn-123")
                .setSenderId("user-456")
                .setReceiverId("receiver-789")  // Added required field
                .setAmount(100.0)
                .setStatus("COMPLETED")  // Added required field
                .setCreatedAt(com.google.protobuf.Timestamp.getDefaultInstance())  // Added required field
                .setUpdatedAt(com.google.protobuf.Timestamp.getDefaultInstance())  // Added required field
                .build();

        serializedEvent = transactionEvent.toByteArray();
    }

    @Test
    void handleSenderTransaction_ShouldProcessValidEvent() throws Exception {
        // Act
        transactionEventConsumer.handleSenderTransaction(serializedEvent);

        // Assert
        verify(rewardService).processTransaction(rewardRequestCaptor.capture());
        RewardRequest capturedRequest = rewardRequestCaptor.getValue();
        
        assertNotNull(capturedRequest);
        assertEquals(transactionEvent.getTransactionId(), capturedRequest.getTransactionId());
        assertEquals(transactionEvent.getSenderId(), capturedRequest.getUserId());
        assertEquals(transactionEvent.getAmount(), capturedRequest.getTransactionAmount());
    }

    @Test
    void handleSenderTransaction_ShouldLogProcessingTime() throws Exception {
        // Arrange
        doAnswer(invocation -> {
            Thread.sleep(100); // Simulate processing time
            return null;
        }).when(rewardService).processTransaction(any(RewardRequest.class));

        // Act
        transactionEventConsumer.handleSenderTransaction(serializedEvent);

        // Assert - Just verify the method completes without exceptions
        verify(rewardService).processTransaction(any(RewardRequest.class));
    }

    @Test
    void handleSenderTransaction_ShouldHandleInvalidProtobuf() {
        // Arrange
        byte[] invalidData = "invalid".getBytes();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionEventConsumer.handleSenderTransaction(invalidData);
        });

        // Verify no interaction with reward service for invalid data
        verifyNoInteractions(rewardService);
    }

    @Test
    void handleSenderTransaction_ShouldLogErrorWhenProcessingFails() throws Exception {
        // Arrange
        String errorMessage = "Processing failed";
        doThrow(new RuntimeException(errorMessage))
                .when(rewardService).processTransaction(any(RewardRequest.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionEventConsumer.handleSenderTransaction(serializedEvent);
        });

    }
}
