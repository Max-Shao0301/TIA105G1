package com.apply.model;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ApplyRepository  extends JpaRepository<ApplyVO, Integer> {

    @Transactional
    @Modifying
    @Query(value="UPDATE apply SET results = ?1, review_time = NOW() WHERE apply_id = ?2 AND review_time IS NULL", nativeQuery = true)
    void updateResults(Integer results, Integer applyId);
}
