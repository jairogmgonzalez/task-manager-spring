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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Entidad que representa una categoría dentro de un tablero.
 */
@Entity
@Table(name = "categories")
public class Category {

    /** ID de la categoría */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de la categoría */
    @Column(name = "name", length = 50, nullable = false)
    @NotBlank
    private String name;

    /** Descripción de la categoría */
    @Column(name = "description", length = 255)
    private String description;

    /** Tablero asociado a la categoría */
    @JsonIgnoreProperties("categories")
    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    /** Conjunto de tareas asociadas a la categoría */
    @JsonIgnoreProperties("category")
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Task> tasks = new HashSet<Task>();

    /** Fecha de creación */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Fecha de última actualización */
    @Column(name = "updated_at", insertable = false, updatable = true)
    private LocalDateTime updatedAt;

    /** Constructor por defecto */
    public Category() {
    }

    /**
     * Constructor con los campos obligatorios.
     *
     * @param name  nombre de la categoría
     * @param board tablero al que pertenece la categoría
     */
    public Category(String name, Board board) {
        this.name = name;
        this.board = board;
    }

    /**
     * Añade una tarea a la categoría.
     *
     * @param task tarea a añadir
     */
    public void addTask(Task task) {
        if (!tasks.contains(task)) {
            tasks.add(task);
            task.setCategory(this);
        }
    }

    /**
     * Elimina una tarea de la categoría.
     *
     * @param task tarea a eliminar
     */
    public void removeTask(Task task) {
        if (tasks.contains(task)) {
            tasks.remove(task);
            task.setCategory(null);
        }
    }

    /**
     * GETTERS Y SETTERS
     */

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Board getBoard() {
        return board;
    }

    /**
     * Establece el tablero al que pertenece la categoría.
     *
     * @param board tablero asociado
     */
    public void setBoard(Board board) {
        this.board = board;

        if (board != null && !board.getCategories().contains(this)) {
            board.addCategory(this);
        }
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    /**
     * Establece el conjunto de tareas asociadas a la categoría.
     *
     * @param tasks conjunto de tareas
     */
    public void setTasks(Set<Task> tasks) {
        this.tasks.clear();

        if (tasks != null) {
            for (Task t : tasks) {
                addTask(t);
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
        return "Category {" +
                "id=" + id +
                ", name='" + name + "'" +
                ", boardId=" + (board != null ? board.getId() : null) +
                "}";
    }

    /** Método equals */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        Category that = (Category) other;
        return Objects.equals(id, that.id);
    }

    /** Método hashCode */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
