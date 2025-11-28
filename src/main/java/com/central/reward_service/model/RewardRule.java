package com.central.reward_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Represents a specific reward outcome (e.g., "₹50 Cashback")
 * linked to a specific Tier.
 */
@Entity
@Data
@Table(name = "reward_rules")
public class RewardRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g., "TIER_1", "TIER_2" - helps us group them
    @Column(nullable = false)
    private String tierName;

    // Minimum transaction amount to trigger this specific tier bucket
    // e.g., Tier 1 starts at 0, Tier 2 starts at 10000
    @Column(nullable = false)
    private Double minTransactionAmount;

    // The Reward details
    private String rewardType;       // CASHBACK, VOUCHER, POINTS
    private String description;      // "5% off on shoes"
    private Double rewardValue;  // 50.00

    // The "GPay" Probability Logic
    // Higher weight = Higher chance of winning
    @Column(nullable = false)
    private int weight; // e.g., 90 for "Better Luck Next Time", 1 for "iPhone"

    private boolean active; // To turn off rewards without deleting rows
}