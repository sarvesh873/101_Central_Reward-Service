package com.central.reward_service.repository;

import com.central.reward_service.model.RewardRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRuleRepository extends JpaRepository<RewardRule, Long> {

    /**
     * Fetches only the rules that are currently active.
     * This allows you to "soft delete" or disable rewards in the DB
     * without deleting the rows.
     */
    List<RewardRule> findByActiveTrue();
}