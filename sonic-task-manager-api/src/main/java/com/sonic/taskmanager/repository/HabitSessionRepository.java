package com.sonic.taskmanager.repository;

import com.sonic.taskmanager.model.HabitSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HabitSessionRepository extends JpaRepository<HabitSession, Long> {

    // Find sessions for a specific task
    List<HabitSession> findByTaskIdOrderBySessionDateDesc(Long taskId);

    // Find sessions for a specific task within date range
    @Query("SELECT h FROM HabitSession h WHERE h.taskId = :taskId " +
           "AND h.sessionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY h.sessionDate DESC")
    List<HabitSession> findByTaskIdAndDateRange(@Param("taskId") Long taskId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    // Find today's session for a task
    Optional<HabitSession> findByTaskIdAndSessionDate(Long taskId, LocalDate sessionDate);

    // Count sessions for a task
    @Query("SELECT COUNT(h) FROM HabitSession h WHERE h.taskId = :taskId")
    long countSessionsByTaskId(@Param("taskId") Long taskId);

    // Find latest session for a task
    @Query("SELECT h FROM HabitSession h WHERE h.taskId = :taskId ORDER BY h.sessionDate DESC LIMIT 1")
    Optional<HabitSession> findLatestSessionByTaskId(@Param("taskId") Long taskId);

    // Find sessions from last N days
    @Query("SELECT h FROM HabitSession h WHERE h.sessionDate >= :sinceDate ORDER BY h.sessionDate DESC")
    List<HabitSession> findRecentSessions(@Param("sinceDate") LocalDate sinceDate);
}
