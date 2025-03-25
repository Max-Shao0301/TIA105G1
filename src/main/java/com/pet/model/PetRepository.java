package com.pet.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PetRepository  extends JpaRepository<PetVO, Integer> {
	
	//這個是寫死只搜尋狀態為啟用的
	@Query(value ="SELECT * FROM pet WHERE mem_id = ?1 AND status = 1", nativeQuery = true)
    List<PetVO> findByMember_MemId(Integer memId);
	
	//這個是讓使用者可以自行選擇要搜尋的狀態  我不太確定哪個比較好
	List<PetVO> findByMember_MemIdAndStatus(Integer memId, Integer status);
	
	@Transactional
	@Modifying
	@Query(value ="UPDATE pet SET status = 0 WHERE pet_id = ?1", nativeQuery = true)
	void disableByPetId(Integer petId);
}
