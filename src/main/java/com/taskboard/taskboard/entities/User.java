package com.taskboard.taskboard.entities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;


/**
 * Entidad que representa un usuario en el sistema.
 */
@Entity
@Table(name = "users")
public class User {

    /** ID del usuario */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de usuario */
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    /** Email del usuario */
    @Column(name = "email", length = 120, nullable = false, unique = true)
    private String email;

    /** Tableros asociados al usuario */
    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Board> boards = new HashSet<Board>();

    /** Fecha de creación */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Fecha de última actualización */
    @Column(name = "updated_at", insertable = false, updatable = true)
    private LocalDateTime updatedAt;

    /** Constructor por defecto */
    public User() {
    }

    /**
     * Constructor con campos obligatorios
     * @param username nombre de usuario
     * @param email email del usuario
     */
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    /**
     * Constructor completo
     * @param username nombre de usuario
     * @param email email del usuario
     * @param boards tableros del usuario
     */
    public User(String username, String email, Set<Board> boards) {
        this.username = username;
        this.email = email;
        this.boards = boards;
    }

    /**
     * Añade un tablero al usuario
     * @param board tablero a añadir
     */
    public void addBoard(Board board) {
        boards.add(board);
        board.setUser(this);
    }

    /**
     * Elimina un tablero del usuario
     * @param board tablero a eliminar
     */
    public void removeBoard(Board board) {
        boards.remove(board);
        board.setUser(null);
    }

    /** GETTERS Y SETTERS */
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Board> getBoards() {
        return boards;
    }

    /**
     * Establece los tableros asociados al usuario.
     *
     * @param boards conjunto de tableros a asociar
     */
    public void setBoards(Set<Board> boards) {
        this.boards = boards;

        if (boards != null) {
            for (Board b : boards) {
                if (b.getUser() != this) {
                    b.setUser(this);
                }
            }
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /** Establece la fecha de creación */
    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** Actualiza la fecha de modificación */
    @PreUpdate
    private void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /** Método toString */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    /** Método equals */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        User that = (User) other;
        return Objects.equals(id, that.id);
    }

    /** Método hashCode */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
