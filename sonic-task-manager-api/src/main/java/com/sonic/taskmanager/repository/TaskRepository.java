package com.sonic.taskmanager.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sonic.taskmanager.model.Task;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find active tasks (not completed, not snoozed)
    @Query("SELECT t FROM Task t WHERE t.status != 'done' AND (t.snoozedUntil IS NULL OR t.snoozedUntil <= :now)")
    List<Task> findActiveTasks(@Param("now") LocalDateTime now);

    // Find tasks by status
    List<Task> findByStatus(String status);

    // Find tasks by type
    List<Task> findByType(String type);
    
    Optional<Task> findByTitle(String title);

    // Find main tasks (no parent)
    List<Task> findByParentIdIsNull();

    // Find subtasks by parent ID
    List<Task> findByParentId(Long parentId);

    // Find tasks with deadline before a certain date
    @Query("SELECT t FROM Task t WHERE t.deadline IS NOT NULL AND t.deadline <= :date AND t.status != 'done'")
    List<Task> findTasksWithDeadlineBefore(@Param("date") LocalDate date);

    // Find overdue tasks
    @Query("SELECT t FROM Task t WHERE t.deadline IS NOT NULL AND t.deadline < :today AND t.status != 'done'")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    // Find urgent tasks (deadline within days)
    @Query("SELECT t FROM Task t WHERE t.deadline IS NOT NULL AND t.deadline BETWEEN :today AND :urgentDate AND t.status != 'done'")
    List<Task> findUrgentTasks(@Param("today") LocalDate today, @Param("urgentDate") LocalDate urgentDate);

    // Find high priority + easy tasks (quick wins)
    @Query("SELECT t FROM Task t WHERE t.priority = 'high' AND t.complexity = 'easy' AND t.status = 'todo' AND t.parentId IS NULL")
    List<Task> findQuickWinTasks();

    // Find reminders that should be shown
    @Query("SELECT t FROM Task t WHERE t.type = 'reminder' AND t.status = 'todo' " +
           "AND (t.snoozedUntil IS NULL OR t.snoozedUntil <= :now) " +
           "AND t.updatedAt <= :reminderThreshold")
    List<Task> findActiveReminders(@Param("now") LocalDateTime now, @Param("reminderThreshold") LocalDateTime reminderThreshold);

    // Find tasks completed today
    @Query("SELECT t FROM Task t WHERE t.status = 'done' AND DATE(t.completedAt) = :today")
    List<Task> findTasksCompletedToday(@Param("today") LocalDate today);

    // Find tasks by priority and status
    @Query("SELECT t FROM Task t WHERE t.priority = :priority AND t.status = :status AND t.parentId IS NULL")
    List<Task> findByPriorityAndStatus(@Param("priority") String priority, @Param("status") String status);

    // Find tasks that need focus calculation (high priority, not completed)
    @Query("SELECT t FROM Task t WHERE t.status IN ('todo', 'doing') AND t.parentId IS NULL " +
           "ORDER BY " +
           "CASE t.priority WHEN 'high' THEN 1 WHEN 'medium' THEN 2 ELSE 3 END, " +
           "CASE WHEN t.deadline < :today THEN 1 " +
           "     WHEN t.deadline <= :urgent THEN 2 " +
           "     ELSE 3 END, " +
           "CASE t.complexity WHEN 'easy' THEN 1 WHEN 'medium' THEN 2 ELSE 3 END")
    List<Task> findTasksForFocusCalculation(@Param("today") LocalDate today, @Param("urgent") LocalDate urgent);

    // Count tasks by status
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    long countTasksByStatus(@Param("status") String status);

    // Find habit tasks
    @Query("SELECT t FROM Task t WHERE t.type = 'habit' AND t.status = 'todo'")
    List<Task> findHabitTasks();

    // === STUDIO/PAGINATION SUPPORT - NEW METHODS ===
    
    // Find all main tasks with pagination and smart ordering
    @Query("SELECT t FROM Task t WHERE t.parentId IS NULL ORDER BY " +
           "CASE WHEN t.deadline IS NOT NULL AND t.deadline < :today THEN 1 " +
           "     WHEN t.deadline IS NOT NULL AND t.deadline <= :urgent THEN 2 " +
           "     ELSE 3 END, " +
           "CASE t.priority WHEN 'high' THEN 1 WHEN 'medium' THEN 2 ELSE 3 END, " +
           "t.createdAt DESC")
    Page<Task> findAllMainTasksPaginated(@Param("today") LocalDate today, 
                                        @Param("urgent") LocalDate urgent, 
                                        Pageable pageable);

    // Find tasks with comprehensive filtering and pagination
    @Query("SELECT t FROM Task t WHERE " +
           "t.parentId IS NULL AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:complexity IS NULL OR t.complexity = :complexity) AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:deadlineFrom IS NULL OR t.deadline IS NULL OR t.deadline >= :deadlineFrom) AND " +
           "(:deadlineTo IS NULL OR t.deadline IS NULL OR t.deadline <= :deadlineTo) AND " +
           "(:searchQuery IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchQuery, '%')) OR " +
           " LOWER(t.description) LIKE LOWER(CONCAT('%', :searchQuery, '%'))) " +
           "ORDER BY " +
           "CASE WHEN t.deadline IS NOT NULL AND t.deadline < :today THEN 1 " +
           "     WHEN t.deadline IS NOT NULL AND t.deadline <= :urgent THEN 2 " +
           "     ELSE 3 END, " +
           "CASE t.priority WHEN 'high' THEN 1 WHEN 'medium' THEN 2 ELSE 3 END, " +
           "t.updatedAt DESC")
    Page<Task> findTasksWithFilter(@Param("status") String status,
                                  @Param("priority") String priority,
                                  @Param("complexity") String complexity,
                                  @Param("type") String type,
                                  @Param("deadlineFrom") LocalDate deadlineFrom,
                                  @Param("deadlineTo") LocalDate deadlineTo,
                                  @Param("searchQuery") String searchQuery,
                                  @Param("today") LocalDate today,
                                  @Param("urgent") LocalDate urgent,
                                  Pageable pageable);

    // Count tasks by various criteria for analytics
    @Query("SELECT COUNT(t) FROM Task t WHERE t.parentId IS NULL AND t.status = :status")
    long countMainTasksByStatus(@Param("status") String status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.parentId IS NULL AND t.priority = :priority")
    long countMainTasksByPriority(@Param("priority") String priority);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.parentId IS NULL AND t.type = :type")
    long countMainTasksByType(@Param("type") String type);

    // Find tasks by multiple IDs (for bulk operations)
    @Query("SELECT t FROM Task t WHERE t.id IN :taskIds")
    List<Task> findByIdIn(@Param("taskIds") List<Long> taskIds);
}