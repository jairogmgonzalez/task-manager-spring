package com.taskboard.taskboard.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskboard.taskboard.entities.Task;
import com.taskboard.taskboard.entities.Task.TaskPriority;
import com.taskboard.taskboard.entities.Task.TaskStatus;
import com.taskboard.taskboard.repositories.TaskRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio para la gestión de Tareas
 */
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private BoardService boardService;

    /**
     * Obtiene tarea por ID
     */
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada con ID: " + id));
    }

    /**
     * Obtiene todas las tareas
     */
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    /**
     * Busca por nombre
     */
    public List<Task> getTaskByName(String name) {
        validateName(name);
        return taskRepository.findByName(name);
    }

    /**
     * Obtiene tareas de categoría
     */
    public List<Task> getTasksByCategoryId(Long categoryId) {
        categoryService.getCategoryById(categoryId);
        return taskRepository.findByCategoryId(categoryId);
    }

    /**
     * Busca por nombre en categoría
     */
    public List<Task> getCategoryTasksByName(Long categoryId, String name) {
        categoryService.getCategoryById(categoryId);
        validateName(name);
        return taskRepository.findCategoryTasksByName(categoryId, name);
    }

    /**
     * Cuenta tareas en categoría
     */
    public Long getCountTasksByCategoryId(Long categoryId) {
        categoryService.getCategoryById(categoryId);
        return taskRepository.countTasksByCategoryId(categoryId);
    }

    /**
     * Busca por estado en categoría
     */
    public List<Task> getCategoryTasksByStatus(Long categoryId, TaskStatus status) {
        categoryService.getCategoryById(categoryId);
        validateStatus(status);
        return taskRepository.findCategoryTasksByStatus(categoryId, status);
    }

    /**
     * Busca por prioridad en categoría
     */
    public List<Task> getCategoryTasksByPriority(Long categoryId, TaskPriority priority) {
        categoryService.getCategoryById(categoryId);
        validatePriority(priority);
        return taskRepository.findCategoryTasksByPriority(categoryId, priority);
    }

    /**
     * Busca todas por estado
     */
    public List<Task> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    /**
     * Busca todas por prioridad
     */
    public List<Task> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    /**
     * Busca alta prioridad
     */
    public List<Task> getHighPriorityTasks() {
        return taskRepository.findHighPriorityTasks();
    }

    /**
     * Busca próximas a vencer
     */
    public List<Task> getUpcomingTasks(Long categoryId, LocalDateTime start, LocalDateTime end) {
        categoryService.getCategoryById(categoryId);
        return taskRepository.findUpcomingTasks(categoryId, start, end);
    }

    /**
     * Busca vencidas
     */
    public List<Task> getOverdueTasks(Long categoryId, LocalDateTime now) {
        categoryService.getCategoryById(categoryId);
        return taskRepository.findOverdueTasks(categoryId, now);
    }

    /**
     * Busca por rango de fechas
     */
    public List<Task> getTasksCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        return taskRepository.findTasksCreatedBetweenDates(startDate, endDate);
    }

    /**
     * Ordena por fecha creación
     */
    public List<Task> getLatestTasks() {
        return taskRepository.findLatestTasks();
    }

    /**
     * Ordena por fecha vencimiento
     */
    public List<Task> getTasksOrderByDueDateDesc(Long categoryId) {
        categoryService.getCategoryById(categoryId);
        return taskRepository.findTasksOrderByDueDateDesc(categoryId);
    }

    /**
     * Cuenta por estado en categoría
     */
    public Long getTasksCountByCategoryAndStatus(Long categoryId, TaskStatus status) {
        categoryService.getCategoryById(categoryId);
        validateStatus(status);
        return taskRepository.countByCategoryIdAndStatus(categoryId, status);
    }

    /**
     * Cuenta por usuario
     */
    public Long getTasksCountByUserId(Long userId) {
        userService.getUserById(userId);
        return taskRepository.countTasksByUserId(userId);
    }

    /**
     * Cuenta por tablero
     */
    public Long getTasksCountByBoardId(Long boardId) {
        boardService.getBoardById(boardId);
        return taskRepository.countTasksByBoardId(boardId);
    }

    /**
     * Crea nueva tarea
     */
    @Transactional
    public Task createTask(Task task) {
        if (task.getId() != null) {
            throw new IllegalArgumentException("Una nueva tarea no debe tener ID");
        }

        validateTask(task);
        categoryService.getCategoryById(task.getCategory().getId());

        task.setStatus(task.getStatus() == null ? TaskStatus.PENDING : task.getStatus());
        task.setPriority(task.getPriority() == null ? TaskPriority.MEDIUM : task.getPriority());

        return taskRepository.save(task);
    }

    /**
     * Actualiza tarea existente
     */
    @Transactional
    public Task updateTask(Task task) {
        Task existingTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la tarea con ID: " + task.getId()));

        if (task.getName() != null) existingTask.setName(task.getName());
        if (task.getDescription() != null) existingTask.setDescription(task.getDescription());
        if (task.getStatus() != null) existingTask.setStatus(task.getStatus());
        if (task.getPriority() != null) existingTask.setPriority(task.getPriority());
        if (task.getDueDate() != null) existingTask.setDueDate(task.getDueDate());

        validateTask(existingTask);
        return taskRepository.save(existingTask);
    }

    /**
     * Elimina tarea
     */
    @Transactional
    public void deleteTask(Long taskId) {
        getTaskById(taskId);
        taskRepository.deleteById(taskId);
    }


    /**
     * Actualiza nombre
     */
    @Transactional
    public void updateTaskName(Long taskId, Long categoryId, String newName) {
        validateName(newName);
        verifyTaskAndCategory(taskId, categoryId);

        int updatedRows = taskRepository.updateTaskName(taskId, categoryId, newName);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar el nombre");
        }
    }

    /**
     * Actualiza descripción
     */
    @Transactional
    public void updateTaskDescription(Long taskId, Long categoryId, String newDescription) {
        validateDescription(newDescription);
        verifyTaskAndCategory(taskId, categoryId);

        int updatedRows = taskRepository.updateTaskDescription(taskId, categoryId, newDescription);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar la descripción");
        }
    }

    /**
     * Actualiza estado
     */
    @Transactional
    public void updateTaskStatus(Long taskId, TaskStatus newStatus) {
        validateStatus(newStatus);
        getTaskById(taskId);

        int updatedRows = taskRepository.updateTaskStatus(taskId, newStatus);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar el status");
        }
    }

    /**
     * Verifica si está vencida
     */
    public boolean isTaskOverdue(Task task) {
        return task.getDueDate() != null &&
                task.getDueDate().isBefore(LocalDateTime.now()) &&
                task.getStatus() != TaskStatus.COMPLETED;
    }

    /**
     * Verifica si está completada
     */
    public boolean isTaskCompleted(Task task) {
        return TaskStatus.COMPLETED.equals(task.getStatus());
    }

    /**
     * MÉTODOS DE VALIDACIÓN
     */
    private void validateTask(Task task) {
        validateName(task.getName());
        if (task.getDescription() != null) validateDescription(task.getDescription());
        if (task.getDueDate() != null) validateDueDate(task.getDueDate());
        if (task.getStatus() != null) validateStatus(task.getStatus());
        if (task.getPriority() != null) validatePriority(task.getPriority());
        if (task.getCategory() == null) {
            throw new IllegalArgumentException("La tarea debe pertenecer a una categoría");
        }
    }

    private void verifyTaskAndCategory(Long taskId, Long categoryId) {
        getTaskById(taskId);
        categoryService.getCategoryById(categoryId);
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
    }

    private void validateDescription(String description) {
        if (description == null && description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía si se proporciona");
        }
    }

    private void validateStatus(TaskStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("El status no puede ser nulo");
        }
    }

    private void validatePriority(TaskPriority priority) {
        if (priority == null) {
            throw new IllegalArgumentException("La prioridad no puede ser nula");
        }
    }

    private void validateDueDate(LocalDateTime dueDate) {
        if (dueDate != null && dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser en el pasado");
        }
    }
}