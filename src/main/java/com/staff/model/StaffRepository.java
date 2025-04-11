// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.staff.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;




public interface StaffRepository extends JpaRepository<StaffVO, Integer> {
	
	//查正常服務人員
	@Query(value = "SELECT * FROM staff WHERE status= 1", nativeQuery = true)
	List<StaffVO> findStaffByOn();
	
	
	@Query(value = "SELECT * FROM staff WHERE staff_email=?1 AND staff_password =?2 AND status= 1", nativeQuery = true)
	Optional<StaffVO> findByLogin(String email, String password);

	//比對雜湊登入
	@Query(value = "SELECT * FROM staff WHERE staff_email=?1 AND status= 1", nativeQuery = true)
	Optional<StaffVO> findByEmail(String email);
	
	@Query(value = "SELECT * FROM staff WHERE staff_email=?1 AND status= 1", nativeQuery = true)
	Optional<StaffVO> findByLoginByEmail(String email);
	
}