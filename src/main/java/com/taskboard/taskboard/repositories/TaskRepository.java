package com.taskboard.taskboard.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taskboard.taskboard.entities.Task;
import com.taskboard.taskboard.entities.Task.TaskPriority;
import com.taskboard.taskboard.entities.Task.TaskStatus;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la gestión de Tareas
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

        /** Busca por nombre exacto */
        List<Task> findByName(String name);

        /** Busca por categoría */
        List<Task> findByCategoryId(Long categoryId);

        /** Busca por nombre en categoría */
        @Query("SELECT t FROM Task t WHERE t.category.id = :categoryId AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
        List<Task> findCategoryTasksByName(@Param("categoryId") Long categoryId, @Param("name") String name);

        /** Busca por estado */
        List<Task> findByStatus(TaskStatus status);

        /** Busca por prioridad */
        List<Task> findByPriority(TaskPriority priority);

        /** Busca por estado en categoría */
        @Query("SELECT t FROM Task t WHERE t.category.id = :categoryId AND t.status = :status")
        List<Task> findCategoryTasksByStatus(@Param("categoryId") Long categoryId, @Param("status") TaskStatus status);

        /** Busca por prioridad en categoría */
        @Query("SELECT t FROM Task t WHERE t.category.id = :categoryId AND t.priority = :priority")
        List<Task> findCategoryTasksByPriority(@Param("categoryId") Long categoryId, @Param("priority") TaskPriority priority);

        /** Busca por prioridad alta */
        @Query("SELECT t FROM Task t WHERE t.priority = 'HIGH' ORDER BY t.createdAt DESC")
        List<Task> findHighPriorityTasks();

        /** Busca tareas próximas */
        @Query("SELECT t FROM Task t WHERE t.category.id = :categoryId AND t.dueDate BETWEEN :start AND :end")
        List<Task> findUpcomingTasks(@Param("categoryId") Long categoryId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

        /** Busca tareas atrasadas */
        @Query("SELECT t FROM Task t WHERE t.category.id = :categoryId AND t.dueDate < :now AND t.status != 'COMPLETED'")
        List<Task> findOverdueTasks(@Param("categoryId") Long categoryId, @Param("now") LocalDateTime now);

        /** Busca por rango de fechas */
        @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :startDate AND :endDate")
        List<Task> findTasksCreatedBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        /** Ordena por fecha de creación */
        @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
        List<Task> findLatestTasks();

        /** Ordena por fecha de vencimiento */
        @Query("SELECT t FROM Task t WHERE t.category.id = :categoryId ORDER BY t.dueDate DESC")
        List<Task> findTasksOrderByDueDateDesc(@Param("categoryId") Long categoryId);

        /** Cuenta por estado en categoría */
        @Query("SELECT COUNT(t) FROM Task t WHERE t.category.id = :categoryId AND t.status = :status")
        long countByCategoryIdAndStatus(@Param("categoryId") Long categoryId, @Param("status") TaskStatus status);

        /** Cuenta por usuario */
        @Query(value = "SELECT COUNT(t.id) FROM tasks t JOIN categories c ON t.category_id = c.id JOIN boards b ON c.board_id = b.id WHERE b.user_id = :userId", nativeQuery = true)
        Long countTasksByUserId(@Param("userId") Long userId);

        /** Cuenta por tablero */
        @Query(value = "SELECT COUNT(t.id) FROM tasks t JOIN categories c ON t.category_id = c.id WHERE c.board_id = :boardId", nativeQuery = true)
        Long countTasksByBoardId(@Param("boardId") Long boardId);

        /** Cuenta por categoría */
        @Query(value = "SELECT COUNT(*) FROM tasks t WHERE t.category_id = :categoryId", nativeQuery = true)
        Long countTasksByCategoryId(@Param("categoryId") Long categoryId);

        /** Actualiza nombre */
        @Modifying
        @Transactional
        @Query("UPDATE Task t SET t.name = :newName WHERE t.id = :taskId AND t.category.id = :categoryId")
        int updateTaskName(@Param("taskId") Long taskId, @Param("categoryId") Long categoryId, @Param("newName") String newName);

        /** Actualiza descripción */
        @Modifying
        @Transactional
        @Query("UPDATE Task t SET t.description = :newDescription WHERE t.id = :taskId AND t.category.id = :categoryId")
        int updateTaskDescription(@Param("taskId") Long taskId, @Param("categoryId") Long categoryId, @Param("newDescription") String newDescription);

        /** Actualiza estado */
        @Modifying
        @Transactional
        @Query("UPDATE Task t SET t.status = :status WHERE t.id = :taskId")
        int updateTaskStatus(@Param("taskId") Long taskId, @Param("status") TaskStatus status);

        /** Actualiza prioridad */
        @Modifying
        @Transactional
        @Query("UPDATE Task t SET t.priority = :priority WHERE t.id = :taskId AND t.category.id = :categoryId")
        int updateTaskPriority(@Param("taskId") Long taskId, @Param("categoryId") Long categoryId, @Param("priority") TaskPriority priority);

        /** Actualiza fecha */
        @Modifying
        @Transactional
        @Query("UPDATE Task t SET t.dueDate = :dueDate WHERE t.id = :taskId AND t.category.id = :categoryId")
        int updateTaskDueDate(@Param("taskId") Long taskId, @Param("categoryId") Long categoryId, @Param("dueDate") LocalDateTime dueDate);
}