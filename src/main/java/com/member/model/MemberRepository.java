package com.member.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;



public interface MemberRepository extends JpaRepository<MemberVO, Integer>{
	
	@Transactional
	@Modifying
	@Query(value="UPDATE member SET status = ?1 WHERE mem_id = ?2 ", nativeQuery = true)
	void updateStatus(Integer status, Integer mem_id);
}
