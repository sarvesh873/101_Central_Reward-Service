package com.central.reward_service.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing a single reward successfully generated and awarded to a user.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rewards")
@Builder
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rewardId;

    // --- Transaction Context ---
    @Column(nullable = false, length = 50)
    private String userId;

    /**
     * ID of the source transaction (Used for Idempotency check).
     */
    @Column(nullable = false, unique = true, length = 100)
    private String transactionId;

    @Column(nullable = false)
    private Double transactionAmount;

    // --- Reward Details (Copied from RewardRule) ---

    /**
     * NEW: Foreign key referencing the specific RewardRule that generated this reward.
     */
    @Column(nullable = false)
    private Long rewardRuleId; // Links back to the reward_rules table primary key

    @Column(nullable = false, length = 50)
    private String rewardType;

    @Column(nullable = false)
    private String rewardDescription;

    @Column(nullable = false)
    private Double rewardValue;

    @Column(length = 255)
    private String redeemCode;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(nullable = false)
    private Timestamp expiresAt; // NEW: When the reward coupon expires.

    private Timestamp claimedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = Timestamp.from(now);
        this.expiresAt = Timestamp.from(now.plus(10, java.time.temporal.ChronoUnit.DAYS));
    }
}