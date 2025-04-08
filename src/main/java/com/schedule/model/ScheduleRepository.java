// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

package com.schedule.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface ScheduleRepository extends JpaRepository<ScheduleVO, Integer> {

	// 服務人員查自己正常的班表
	@Query(value = "SELECT * FROM schedule WHERE staff_id =?1 AND status= 1", nativeQuery = true)
	List<ScheduleVO> findByStaffOn(int staff_id);

	// 查被可預約的班表
	@Query(value = "SELECT * FROM schedule WHERE status= 1", nativeQuery = true)
	List<ScheduleVO> findScheduleByOn();

	// 比對該時間沒有重複
	@Query(value = "SELECT COUNT(*) FROM schedule WHERE staff_id =?1 AND date =?2 AND timeslot =?3 AND status= 1", nativeQuery = true)
	int checkScheduleUnique(Integer staffId, LocalDate date, String timeslot);

	// 用日期、預約時間查詢可工作的服務人員  使用join fetch 避免查班表拿員工資料的N+1問題
	@Query("SELECT s FROM ScheduleVO s JOIN FETCH s.staffVO WHERE FUNCTION('substring', s.timeslot, ?2, 3) = '111' AND s.date = ?1")
	List<ScheduleVO> findByBookableStaff(LocalDate date, Integer appttime);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE schedule SET timeslot = REPLACE(timeslot, '2', '1') WHERE sch_id = ?1 ", nativeQuery = true)
	void updateUnbooked(Integer schId);
	
	@Transactional
	@Modifying
	@Query(value="UPDATE schedule SET timeslot = REPLACE(timeslot, '1', '2') WHERE sch_id = ?1 ", nativeQuery = true)
	void updateBooked(Integer schId);
}
