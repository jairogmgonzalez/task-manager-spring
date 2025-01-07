package com.taskboard.taskboard.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taskboard.taskboard.entities.User;

import jakarta.transaction.Transactional;

/**
 * Repositorio para la gestión de Usuarios
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su email
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un username
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un email
     */
    boolean existsByEmail(String email);

    /**
     * Cuenta el total de usuarios
     */
    @Query("SELECT COUNT(u) FROM User u")
    long countUsers();

    /**
     * Actualiza el username de un usuario
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.username = :newUsername WHERE u.id = :userId")
    int updateUsername(@Param("userId") Long userId, @Param("newUsername") String newUsername);

    /**
     * Actualiza el email de un usuario
     */
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.email = :newEmail WHERE u.id = :userId")
    int updateEmail(@Param("userId") Long userId, @Param("newEmail") String newEmail);

    /**
     * Busca usuarios por coincidencia en username
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByUsernameContaining(@Param("keyword") String keyword);

    /**
     * Busca usuarios por dominio de email
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %:domain")
    List<User> findUsersByEmailDomain(@Param("domain") String domain);

    /**
     * Encuentra usuarios con el username más largo
     */
    @Query(value = "SELECT * FROM users WHERE LENGTH(username) = (SELECT MAX(LENGTH(username)) FROM users)", nativeQuery = true)
    List<User> findUsersWithLongestUsername();

    /**
     * Encuentra usuarios con tableros
     */
    @Query("SELECT u FROM User u WHERE u.boards IS NOT EMPTY")
    List<User> findUsersWithBoards();

    /**
     * Encuentra usuarios sin tableros
     */
    @Query("SELECT u FROM User u WHERE u.boards IS EMPTY")
    List<User> findUsersWithoutBoards();

    /**
     * Encuentra usuarios con más de X tableros
     */
    @Query(value = "SELECT u.username FROM users u " +
            "JOIN boards b ON u.id = b.user_id " +
            "GROUP BY u.id, u.username " +
            "HAVING COUNT(b.id) > :minBoards", nativeQuery = true)
    List<String> findUsernamesWithMoreThanXBoards(@Param("minBoards") Long minBoards);
}