package com.taskboard.taskboard.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taskboard.taskboard.entities.Category;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la gestión de Categorías
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

        /**
         * Busca por nombre exacto
         */
        List<Category> findByName(String name);

        /**
         * Busca por ID de tablero
         */
        List<Category> findByBoardId(Long boardId);

        /**
         * Busca por nombre en tablero
         */
        @Query("SELECT c FROM Category c WHERE c.board.id = :boardId AND LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
        List<Category> findBoardCategoriesByName(@Param("boardId") Long boardId, @Param("name") String name);

        /**
         * Cuenta por tablero
         */
        @Query("SELECT COUNT(c) FROM Category c WHERE c.board.id = :boardId")
        Long countCategoriesByBoardId(@Param("boardId") Long boardId);

        /**
         * Cuenta por usuario
         */
        @Query(value = "SELECT COUNT(c.id) FROM categories c JOIN boards b ON c.board_id = b.id WHERE b.user_id = :userId",
                nativeQuery = true)
        Long countCategoriesByUserId(@Param("userId") Long userId);

        /**
         * Cuenta tareas de categoría
         */
        @Query("SELECT COUNT(t) FROM Category c JOIN c.tasks t WHERE c.id = :categoryId")
        Long countTasksInCategory(@Param("categoryId") Long categoryId);

        /**
         * Busca con tareas en tablero
         */
        @Query("SELECT c FROM Category c WHERE c.board.id = :boardId and c.tasks IS NOT EMPTY")
        List<Category> findBoardCategoriesWithTasks(@Param("boardId") Long boardId);

        /**
         * Busca sin tareas en tablero
         */
        @Query("SELECT c FROM Category c WHERE c.board.id = :boardId and c.tasks IS EMPTY")
        List<Category> findBoardCategoriesWithoutTasks(@Param("boardId") Long boardId);

        /**
         * Busca todas con tareas
         */
        @Query("SELECT c FROM Category c WHERE c.tasks IS NOT EMPTY")
        List<Category> findCategoriesWithTasks();

        /**
         * Busca todas sin tareas
         */
        @Query("SELECT c FROM Category c WHERE c.tasks IS EMPTY")
        List<Category> findCategoriesWithoutTasks();

        /**
         * Ordena por cantidad de tareas
         */
        @Query("SELECT c FROM Category c LEFT JOIN c.tasks t WHERE c.board.id = :boardId GROUP BY c ORDER BY COUNT(t) DESC")
        List<Category> findBoardCategoriesOrderByTaskCount(@Param("boardId") Long boardId);

        /**
         * Busca por fecha de creación
         */
        @Query("SELECT c FROM Category c WHERE c.createdAt > :date")
        List<Category> findCategoriesCreatedAfter(@Param("date") LocalDateTime date);

        /**
         * Actualiza nombre
         */
        @Modifying
        @Transactional
        @Query("UPDATE Category c SET c.name = :newName WHERE c.id = :id")
        int updateCategoryName(@Param("id") Long id, @Param("newName") String newName);

        /**
         * Actualiza descripción en tablero
         */
        @Modifying
        @Transactional
        @Query("UPDATE Category c SET c.description = :newDescription WHERE c.id = :categoryId AND c.board.id = :boardId")
        int updateCategoryDescription(@Param("categoryId") Long categoryId, @Param("boardId") Long boardId,
                                      @Param("newDescription") String newDescription);

}