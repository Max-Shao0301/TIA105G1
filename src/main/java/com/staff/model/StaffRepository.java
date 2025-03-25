// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.staff.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;




public interface StaffRepository extends JpaRepository<StaffVO, Integer> {
	
	//查正常服務人員
	@Query(value = "SELECT * FROM staff WHERE status= 1", nativeQuery = true)
	List<StaffVO> findStaffByOn();
	
}