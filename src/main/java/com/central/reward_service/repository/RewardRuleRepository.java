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

    /**
     * Finds all reward rules for a specific tier name.
     * @param tierName The name of the tier to find rules for
     * @return List of reward rules matching the tier name
     */
    List<RewardRule> findByTierName(String tierName);
}