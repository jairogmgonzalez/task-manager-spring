package com.taskboard.taskboard.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taskboard.taskboard.entities.Board;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la gestión de Tableros
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * Obtiene todos los tableros de un usuario.
     *
     * @param userId ID del usuario
     * @return lista de tableros asociados al usuario
     */
    List<Board> findByUserId(Long userId);

    /**
     * Busca tableros de un usuario por nombre.
     *
     * @param userId ID del usuario
     * @param name   nombre del tablero (o parte del nombre) para buscar
     * @return lista de tableros que coinciden con el nombre
     */
    @Query("SELECT b FROM Board b WHERE b.user.id = :userId AND LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Board> findUserBoardsByName(@Param("userId") Long userId, @Param("name") String name);

    /**
     * Obtiene el número de tableros de un usuario.
     *
     * @param userId ID del usuario
     * @return número de tableros asociados al usuario
     */
    @Query("SELECT COUNT(b) FROM Board b WHERE b.user.id = :userId")
    Long countBoardsByUserId(@Param("userId") Long userId);

    /**
     * Busca tableros de un usuario que tienen categorías.
     *
     * @param userId ID del usuario
     * @return lista de tableros con al menos una categoría
     */
    @Query("SELECT b FROM Board b WHERE b.user.id = :userId AND b.categories IS NOT EMPTY")
    List<Board> findUserBoardsWithCategories(@Param("userId") Long userId);

    /**
     * Busca tableros de un usuario sin categorías.
     *
     * @param userId ID del usuario
     * @return lista de tableros sin categorías
     */
    @Query("SELECT b FROM Board b WHERE b.user.id = :userId AND b.categories IS EMPTY")
    List<Board> findUserBoardsWithoutCategories(@Param("userId") Long userId);

    /**
     * Busca tableros de un usuario creados antes de una fecha.
     *
     * @param userId ID del usuario
     * @param date   fecha límite
     * @return lista de tableros creados antes de la fecha proporcionada
     */
    @Query("SELECT b FROM Board b WHERE b.user.id = :userId AND b.createdAt < :date")
    List<Board> findUserBoardsCreatedBefore(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    /**
     * Busca tableros de un usuario creados después de una fecha.
     *
     * @param userId ID del usuario
     * @param date   fecha límite
     * @return lista de tableros creados después de la fecha proporcionada
     */
    @Query("SELECT b FROM Board b WHERE b.user.id = :userId AND b.createdAt > :date")
    List<Board> findUserBoardsCreatedAfter(@Param("userId") Long userId, @Param("date") LocalDateTime date);

    /**
     * Actualiza el nombre de un tablero.
     *
     * @param boardId ID del tablero
     * @param newName nuevo nombre del tablero
     * @return número de filas actualizadas
     */
    @Modifying
    @Transactional
    @Query("UPDATE Board b SET b.name = :newName WHERE b.id = :boardId")
    int updateBoardName(@Param("boardId") Long boardId, @Param("newName") String newName);

    /**
     * Actualiza la descripción de un tablero.
     *
     * @param boardId        ID del tablero
     * @param newDescription nueva descripción del tablero
     * @return número de filas actualizadas
     */
    @Modifying
    @Transactional
    @Query("UPDATE Board b SET b.description = :newDescription WHERE b.id = :boardId")
    int updateBoardDescription(@Param("boardId") Long boardId, @Param("newDescription") String newDescription);

    /**
     * Busca tableros por su nombre exacto.
     *
     * @param name nombre del tablero
     * @return lista de tableros con el nombre especificado
     */
    List<Board> findByName(String name);

    /**
     * Busca tableros creados después de una fecha específica.
     *
     * @param date fecha límite para la búsqueda
     * @return lista de tableros creados después de la fecha especificada
     */
    @Query("SELECT b FROM Board b WHERE b.createdAt > :date")
    List<Board> findBoardsCreatedAfter(@Param("date") LocalDateTime date);

    /**
     * Cuenta el número de tableros que tienen una descripción definida.
     *
     * @return número de tableros con descripción
     */
    @Query("SELECT COUNT(b) FROM Board b WHERE b.description IS NOT NULL")
    Long countBoardsWithDescription();

    /**
     * Busca tableros que tienen un mínimo de categorías asociadas.
     *
     * @param minCategories número mínimo de categorías
     * @return lista de tableros que cumplen con el criterio
     */
    @Query(value = "SELECT b.id, b.name, b.description, b.user_id, b.created_at, b.updated_at " +
            "FROM boards b " +
            "JOIN categories c ON b.id = c.board_id " +
            "GROUP BY b.id, b.name, b.description, b.user_id, b.created_at, b.updated_at " +
            "HAVING COUNT(c.id) >= :minCategories", nativeQuery = true)
    List<Board> findBoardsWithMinCategories(@Param("minCategories") Long minCategories);

    /**
     * Busca tableros que tienen más de un número específico de tareas asociadas.
     *
     * @param minTasks número mínimo de tareas
     * @return lista de tableros con más tareas que el número especificado
     */
    @Query(value = "SELECT b.* FROM boards b " +
            "JOIN categories c ON b.id = c.board_id " +
            "JOIN tasks t ON c.id = t.category_id " +
            "GROUP BY b.id " +
            "HAVING COUNT(t.id) > :minTasks", nativeQuery = true)
    List<Board> findBoardsWithMoreThanXTasks(@Param("minTasks") Long minTasks);

    /**
     * Busca tableros que no tienen categorías asociadas.
     *
     * @return lista de tableros sin categorías
     */
    @Query("SELECT b FROM Board b WHERE b.categories IS EMPTY")
    List<Board> findBoardsWithoutCategories();

    /**
     * Busca tableros que tienen al menos una categoría asociada.
     *
     * @return lista de tableros con categorías
     */
    @Query("SELECT b FROM Board b WHERE b.categories IS NOT EMPTY")
    List<Board> findBoardsWithCategories();

}
