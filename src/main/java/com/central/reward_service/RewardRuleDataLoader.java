package com.central.reward_service;

import com.central.reward_service.model.RewardRule;
import com.central.reward_service.repository.RewardRuleRepository; // Assuming you have this repository
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Arrays;

/**
 * Inserts initial RewardRule data into the 'reward_rules' table
 * only if the table is currently empty.
 */
@Component
public class RewardRuleDataLoader implements CommandLineRunner {

    private final RewardRuleRepository rewardRuleRepository;

    // Spring automatically injects the repository
    public RewardRuleDataLoader(RewardRuleRepository rewardRuleRepository) {
        this.rewardRuleRepository = rewardRuleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Check if the table already has data
        long count = rewardRuleRepository.count();

        if (count == 0) {
            System.out.println("🤖 Initializing Reward Rules data...");
            // 2. Define the sample data
            List<RewardRule> initialRules = createInitialRewardRules();

            // 3. Save all entities to the database
            rewardRuleRepository.saveAll(initialRules);
            System.out.println("✅ Successfully inserted " + initialRules.size() + " initial Reward Rules.");
        } else {
            System.out.println("ℹ️ Reward Rules table already contains " + count + " records. Skipping initialization.");
        }
    }

    // Helper method to create the list of RewardRule objects based on your SQL
    private List<RewardRule> createInitialRewardRules() {
        return Arrays.asList(
                // TIER 1
                createRule("TIER_1", 0.0, "BETTER_LUCK", "😔 Try Again Next Time", null, 35, true),
                createRule("TIER_1", 0.0, "CASHBACK", "₹2 Cashback", 2.00, 25, true),
                createRule("TIER_1", 0.0, "POINTS", "5 Reward Points", 5.00, 15, true),
                createRule("TIER_1", 0.0, "COUPON", "5% off on Groceries", null, 10, true),
                createRule("TIER_1", 0.0, "VOUCHER", "₹10 off on First Food Order", 10.00, 5, true),
                createRule("TIER_1", 0.0, "POINTS", "10 Reward Points", 10.00, 4, true),
                createRule("TIER_1", 0.0, "CASHBACK", "₹5 Cashback", 5.00, 3, true),
                createRule("TIER_1", 0.0, "COUPON", "Free Shipping (Up to ₹50)", null, 2, true),
                createRule("TIER_1", 0.0, "VOUCHER", "₹20 Store Voucher", 20.00, 1, true),

                // TIER 2
                createRule("TIER_2", 100.0, "CASHBACK", "₹10 Cashback", 10.00, 25, true),
                createRule("TIER_2", 100.0, "VOUCHER", "20% off Swiggy (Max ₹75)", null, 20, true),
                createRule("TIER_2", 100.0, "POINTS", "50 Reward Points", 50.00, 15, true),
                createRule("TIER_2", 100.0, "DISCOUNT", "Flat ₹50 off on next bill", 50.00, 12, true),
                createRule("TIER_2", 100.0, "CASHBACK", "₹25 Cashback", 25.00, 10, true),
                createRule("TIER_2", 100.0, "VOUCHER", "Free Coffee Voucher (Starbucks)", 200.00, 8, true),
                createRule("TIER_2", 100.0, "VOUCHER", "₹100 Shopping Voucher", 100.00, 5, true),
                createRule("TIER_2", 100.0, "POINTS", "100 Reward Points", 100.00, 3, true),
                createRule("TIER_2", 100.0, "JACKPOT", "⭐ ₹500 Amazon Gift Card", 500.00, 2, true),

                // TIER 3
                createRule("TIER_3", 1000.0, "SCRATCH_CARD", "Mystery Scratch Card (Up to ₹200)", null, 30, true),
                createRule("TIER_3", 1000.0, "CASHBACK", "₹50 Cashback", 50.00, 25, true),
                createRule("TIER_3", 1000.0, "VOUCHER", "₹250 off on Fashion", 250.00, 15, true),
                createRule("TIER_3", 1000.0, "MOVIE_TICKET", "BOGO Movie Ticket Voucher", 500.00, 10, true),
                createRule("TIER_3", 1000.0, "CASHBACK", "₹100 Cashback", 100.00, 8, true),
                createRule("TIER_3", 1000.0, "POINTS", "500 Reward Points", 500.00, 6, true),
                createRule("TIER_3", 1000.0, "GIFT_CARD", "₹500 Food Gift Card", 500.00, 4, true),
                createRule("TIER_3", 1000.0, "SUPER_JACKPOT", "₹1000 Cashback", 1000.00, 2, true),

                // TIER 4
                createRule("TIER_4", 10000.0, "VOUCHER", "₹1500 off on Domestic Flights", null, 20, true),
                createRule("TIER_4", 10000.0, "CASHBACK", "₹1000 Cashback", 1000.00, 20, true),
                createRule("TIER_4", 10000.0, "DISCOUNT", "20% off on Gadgets (Max ₹2500)", 2500.00, 15, true),
                createRule("TIER_4", 10000.0, "HOTEL_VOUCHER", "25% off on Hotel Bookings", null, 15, true),
                createRule("TIER_4", 10000.0, "POINTS", "2500 Reward Points", 2500.00, 10, true),
                createRule("TIER_4", 10000.0, "GIFT_CARD", "₹3000 Electronics Voucher", 3000.00, 8, true),
                createRule("TIER_4", 10000.0, "MEGA_CASHBACK", "₹2500 Cashback", 2500.00, 7, true),
                createRule("TIER_4", 10000.0, "SUPER_JACKPOT", "✈️ Free Return Flight (Max ₹7k)", 7000.00, 5, true),

                // TIER 5
                createRule("TIER_5", 100000.0, "CASHBACK", "₹5000 Cashback", 5000.00, 25, true),
                createRule("TIER_5", 100000.0, "FLIGHT_VOUCHER", "₹10,000 off on International Flights", 10000.00, 20, true),
                createRule("TIER_5", 100000.0, "DISCOUNT", "30% off on Luxury Watches (Max ₹10k)", 10000.00, 15, true),
                createRule("TIER_5", 100000.0, "POINTS", "10,000 Reward Points", 10000.00, 12, true),
                createRule("TIER_5", 100000.0, "GIFT_CARD", "₹15,000 Apple Store Voucher", 15000.00, 10, true),
                createRule("TIER_5", 100000.0, "HOTEL_VOUCHER", "2-Night Hotel Stay Voucher (Max ₹8k)", 8000.00, 8, true),
                createRule("TIER_5", 100000.0, "MEGA_CASHBACK", "₹10,000 Cashback", 10000.00, 7, true),
                createRule("TIER_5", 100000.0, "ULTRA_JACKPOT", "💎 ₹25,000 Gold Voucher", 25000.00, 3, true),

                // TIER 6
                createRule("TIER_6", 500000.0, "CASHBACK", "₹25,000 Cashback", 25000.00, 30, true),
                createRule("TIER_6", 500000.0, "FLIGHT_VOUCHER", "₹50,000 off on Business Class Flight", 50000.00, 25, true),
                createRule("TIER_6", 500000.0, "GIFT_CARD", "₹30,000 Luxury Shopping Voucher", 30000.00, 20, true),
                createRule("TIER_6", 500000.0, "POINTS", "50,000 Reward Points", 50000.00, 10, true),
                createRule("TIER_6", 500000.0, "MEGA_JACKPOT", "MacBook Air M3 Voucher (Max ₹1,00,000)", 100000.00, 8, true),
                createRule("TIER_6", 500000.0, "ELITE_JACKPOT", "🔥 ₹1,50,000 Trip Voucher (Travel/Stay)", 150000.00, 5, true),
                createRule("TIER_6", 500000.0, "SUPER_ELITE", "👑 ₹2,00,000 Mega Cashback", 200000.00, 2, true)
        );
    }

    // Utility method to build a RewardRule object
    private RewardRule createRule(String tierName, Double minTransactionAmount, String rewardType, String description, Double rewardValue, int weight, boolean active) {
        RewardRule rule = new RewardRule();
        rule.setTierName(tierName);
        rule.setMinTransactionAmount(minTransactionAmount);
        rule.setRewardType(rewardType);
        rule.setDescription(description);
        rule.setRewardValue(rewardValue);
        rule.setWeight(weight);
        rule.setActive(active);
        // ID and 'rewards' list are managed by JPA/Lombok and left blank
        return rule;
    }
}
