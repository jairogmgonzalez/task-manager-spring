package com.taskboard.taskboard.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Entidad que representa una tarea dentro de una categoría.
 */
@Entity
@Table(name = "tasks")
public class Task {

    /**
     * Enumeración para el estado de la tarea.
     */
    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED
    }

    /**
     * Enumeración para la prioridad de la tarea.
     */
    public enum TaskPriority {
        LOW,
        MEDIUM,
        HIGH
    }

    /** ID de la tarea */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre de la tarea */
    @Column(name = "name", length = 50, nullable = false)
    @NotBlank
    private String name;

    /** Descripción de la tarea */
    @Column(name = "description", length = 255)
    private String description;

    /** Estado de la tarea */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    /** Prioridad de la tarea */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TaskPriority priority;

    /** Fecha límite de la tarea */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /** Categoría asociada a la tarea */
    @JsonIgnoreProperties("tasks")
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    /** Fecha de creación */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Fecha de última actualización */
    @Column(name = "updated_at", insertable = false, updatable = true)
    private LocalDateTime updatedAt;

    /** Constructor por defecto */
    public Task() {
    }

    /**
     * Constructor con campos obligatorios.
     *
     * @param name     nombre de la tarea
     * @param category categoría asociada a la tarea
     */
    public Task(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    /**
     * Constructor completo.
     *
     * @param name        nombre de la tarea
     * @param description descripción de la tarea
     * @param status      estado de la tarea
     * @param priority    prioridad de la tarea
     * @param dueDate     fecha límite de la tarea
     * @param category    categoría asociada
     */
    public Task(String name, String description, TaskStatus status, TaskPriority priority, LocalDateTime dueDate,
                Category category) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.category = category;
    }

    /** GETTERS Y SETTERS */

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Category getCategory() {
        return category;
    }

    /**
     * Establece la categoría asociada a la tarea.
     * Si la categoría no contiene esta tarea, la añade.
     *
     * @param category categoría asociada
     */
    public void setCategory(Category category) {
        this.category = category;

        if (category != null && !category.getTasks().contains(this)) {
            category.addTask(this);
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
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                ", categoryId=" + (category != null ? category.getId() : null) +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /** Método equals */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other == null || getClass() != other.getClass())
            return false;

        Task that = (Task) other;
        return Objects.equals(id, that.id);
    }

    /** Método hashCode */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
