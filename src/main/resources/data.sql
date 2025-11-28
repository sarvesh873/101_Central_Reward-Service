-- This SQL script populates the 'reward_rules' table with 6 tiers,
-- covering all transaction amounts from 0 up to 10,00,000 (10 Lakhs).

-- Clear existing data to ensure a fresh start
DELETE FROM reward_rules;

-- =============================================
-- 1. TIER 1: Micro Transactions (₹0 - ₹99)
-- Strategy: Better Luck Next Time and minimal rewards.
-- Min Amount: 0
-- =============================================

INSERT INTO reward_rules (tier_name, min_transaction_amount, reward_type, description, reward_value, weight, active) VALUES
                                                                                                                         ('TIER_1', 0, 'BETTER_LUCK', '😔 Try Again Next Time', NULL, 35, true),
                                                                                                                         ('TIER_1', 0, 'CASHBACK', '₹2 Cashback', 2.00, 25, true),
                                                                                                                         ('TIER_1', 0, 'POINTS', '5 Reward Points', 5.00, 15, true),
                                                                                                                         ('TIER_1', 0, 'COUPON', '5% off on Groceries', NULL, 10, true),
                                                                                                                         ('TIER_1', 0, 'VOUCHER', '₹10 off on First Food Order', 10.00, 5, true),
                                                                                                                         ('TIER_1', 0, 'POINTS', '10 Reward Points', 10.00, 4, true),
                                                                                                                         ('TIER_1', 0, 'CASHBACK', '₹5 Cashback', 5.00, 3, true),
                                                                                                                         ('TIER_1', 0, 'COUPON', 'Free Shipping (Up to ₹50)', NULL, 2, true),
                                                                                                                         ('TIER_1', 0, 'VOUCHER', '₹20 Store Voucher', 20.00, 1, true);

-- =============================================
-- 2. TIER 2: Small Transactions (₹100 - ₹999)
-- Strategy: Small cashback and brand vouchers.
-- Min Amount: 100
-- =============================================

INSERT INTO reward_rules (tier_name, min_transaction_amount, reward_type, description, reward_value, weight, active) VALUES
                                                                                                                         ('TIER_2', 100, 'CASHBACK', '₹10 Cashback', 10.00, 25, true),
                                                                                                                         ('TIER_2', 100, 'VOUCHER', '20% off Swiggy (Max ₹75)', NULL, 20, true),
                                                                                                                         ('TIER_2', 100, 'POINTS', '50 Reward Points', 50.00, 15, true),
                                                                                                                         ('TIER_2', 100, 'DISCOUNT', 'Flat ₹50 off on next bill', 50.00, 12, true),
                                                                                                                         ('TIER_2', 100, 'CASHBACK', '₹25 Cashback', 25.00, 10, true),
                                                                                                                         ('TIER_2', 100, 'VOUCHER', 'Free Coffee Voucher (Starbucks)', 200.00, 8, true),
                                                                                                                         ('TIER_2', 100, 'VOUCHER', '₹100 Shopping Voucher', 100.00, 5, true),
                                                                                                                         ('TIER_2', 100, 'POINTS', '100 Reward Points', 100.00, 3, true),
                                                                                                                         ('TIER_2', 100, 'JACKPOT', '⭐ ₹500 Amazon Gift Card', 500.00, 2, true);

-- =============================================
-- 3. TIER 3: Medium Transactions (₹1,000 - ₹9,999)
-- Strategy: Mid-level vouchers, scratch cards.
-- Min Amount: 1000
-- =============================================

INSERT INTO reward_rules (tier_name, min_transaction_amount, reward_type, description, reward_value, weight, active) VALUES
                                                                                                                         ('TIER_3', 1000, 'SCRATCH_CARD', 'Mystery Scratch Card (Up to ₹200)', NULL, 30, true),
                                                                                                                         ('TIER_3', 1000, 'CASHBACK', '₹50 Cashback', 50.00, 25, true),
                                                                                                                         ('TIER_3', 1000, 'VOUCHER', '₹250 off on Fashion', 250.00, 15, true),
                                                                                                                         ('TIER_3', 1000, 'MOVIE_TICKET', 'BOGO Movie Ticket Voucher', 500.00, 10, true),
                                                                                                                         ('TIER_3', 1000, 'CASHBACK', '₹100 Cashback', 100.00, 8, true),
                                                                                                                         ('TIER_3', 1000, 'POINTS', '500 Reward Points', 500.00, 6, true),
                                                                                                                         ('TIER_3', 1000, 'GIFT_CARD', '₹500 Food Gift Card', 500.00, 4, true),
                                                                                                                         ('TIER_3', 1000, 'SUPER_JACKPOT', '₹1000 Cashback', 1000.00, 2, true);

-- =============================================
-- 4. TIER 4: Large Transactions (₹10,000 - ₹99,999)
-- Strategy: Focus on higher value items, travel, and electronics.
-- Min Amount: 10000
-- =============================================

INSERT INTO reward_rules (tier_name, min_transaction_amount, reward_type, description, reward_value, weight, active) VALUES
                                                                                                                         ('TIER_4', 10000, 'VOUCHER', '₹1500 off on Domestic Flights', NULL, 20, true),
                                                                                                                         ('TIER_4', 10000, 'CASHBACK', '₹1000 Cashback', 1000.00, 20, true),
                                                                                                                         ('TIER_4', 10000, 'DISCOUNT', '20% off on Gadgets (Max ₹2500)', 2500.00, 15, true),
                                                                                                                         ('TIER_4', 10000, 'HOTEL_VOUCHER', '25% off on Hotel Bookings', NULL, 15, true),
                                                                                                                         ('TIER_4', 10000, 'POINTS', '2500 Reward Points', 2500.00, 10, true),
                                                                                                                         ('TIER_4', 10000, 'GIFT_CARD', '₹3000 Electronics Voucher', 3000.00, 8, true),
                                                                                                                         ('TIER_4', 10000, 'MEGA_CASHBACK', '₹2500 Cashback', 2500.00, 7, true),
                                                                                                                         ('TIER_4', 10000, 'SUPER_JACKPOT', '✈️ Free Return Flight (Max ₹7k)', 7000.00, 5, true);

-- =============================================
-- 5. TIER 5: Premium Transactions (₹1,00,000 - ₹4,99,999)
-- Strategy: Significant value, luxury travel, and electronics.
-- Min Amount: 100000
-- =============================================

INSERT INTO reward_rules (tier_name, min_transaction_amount, reward_type, description, reward_value, weight, active) VALUES
                                                                                                                         ('TIER_5', 100000, 'CASHBACK', '₹5000 Cashback', 5000.00, 25, true),
                                                                                                                         ('TIER_5', 100000, 'FLIGHT_VOUCHER', '₹10,000 off on International Flights', 10000.00, 20, true),
                                                                                                                         ('TIER_5', 100000, 'DISCOUNT', '30% off on Luxury Watches (Max ₹10k)', 10000.00, 15, true),
                                                                                                                         ('TIER_5', 100000, 'POINTS', '10,000 Reward Points', 10000.00, 12, true),
                                                                                                                         ('TIER_5', 100000, 'GIFT_CARD', '₹15,000 Apple Store Voucher', 15000.00, 10, true),
                                                                                                                         ('TIER_5', 100000, 'HOTEL_VOUCHER', '2-Night Hotel Stay Voucher (Max ₹8k)', 8000.00, 8, true),
                                                                                                                         ('TIER_5', 100000, 'MEGA_CASHBACK', '₹10,000 Cashback', 10000.00, 7, true),
                                                                                                                         ('TIER_5', 100000, 'ULTRA_JACKPOT', '💎 ₹25,000 Gold Voucher', 25000.00, 3, true);

-- =============================================
-- 6. TIER 6: Elite Transactions (₹5,00,000 - ₹10,00,000)
-- Strategy: The highest value, rarest rewards.
-- Min Amount: 500000
-- =============================================

INSERT INTO reward_rules (tier_name, min_transaction_amount, reward_type, description, reward_value, weight, active) VALUES
                                                                                                                         ('TIER_6', 500000, 'CASHBACK', '₹25,000 Cashback', 25000.00, 30, true),
                                                                                                                         ('TIER_6', 500000, 'FLIGHT_VOUCHER', '₹50,000 off on Business Class Flight', 50000.00, 25, true),
                                                                                                                         ('TIER_6', 500000, 'GIFT_CARD', '₹30,000 Luxury Shopping Voucher', 30000.00, 20, true),
                                                                                                                         ('TIER_6', 500000, 'POINTS', '50,000 Reward Points', 50000.00, 10, true),
                                                                                                                         ('TIER_6', 500000, 'MEGA_JACKPOT', 'MacBook Air M3 Voucher (Max ₹1,00,000)', 100000.00, 8, true),
                                                                                                                         ('TIER_6', 500000, 'ELITE_JACKPOT', '🔥 ₹1,50,000 Trip Voucher (Travel/Stay)', 150000.00, 5, true),
                                                                                                                         ('TIER_6', 500000, 'SUPER_ELITE', '👑 ₹2,00,000 Mega Cashback', 200000.00, 2, true);