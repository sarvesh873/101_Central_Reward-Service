package com.central.reward_service.repository;

import com.central.reward_service.model.Reward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {

    // Fetch rewards for a user with pagination (e.g., latest 10)
    Page<Reward> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Check if a reward already exists for this transaction (Idempotency)
    boolean existsByTransactionId(String transactionId);
}