package com.central.reward_service.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.sql.Timestamp;
import java.time.Instant;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardStatus status;

    private Timestamp claimedAt;

    /**
     * MAPPING: Defines the Many-to-One relationship to the parent RewardRule.
     * The @JoinColumn specifies the foreign key column in the 'rewards' table
     * that links to the primary key (id) of the 'reward_rules' table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_rule_id", nullable = false) // Explicitly set the name

    private RewardRule rewardRule;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = Timestamp.from(now);
        this.expiresAt = Timestamp.from(now.plus(10, java.time.temporal.ChronoUnit.DAYS));
        this.status = RewardStatus.UNCLAIMED;
    }

    @PreUpdate
    public void updateStatus() {
        if (status != RewardStatus.CLAIMED) {
            if (expiresAt != null && expiresAt.before(new Timestamp(System.currentTimeMillis()))) {
                status = RewardStatus.EXPIRED;
            }
        }
    }
}