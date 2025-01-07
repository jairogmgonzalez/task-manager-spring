package com.taskboard.taskboard.controllers.api;

import com.taskboard.taskboard.entities.Task;
import com.taskboard.taskboard.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de Tareas
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskAPIController {

    @Autowired
    TaskService taskService;

    /**
     * Crea nueva tarea
     */
    @PostMapping("/add")
    public Task addTask(@RequestBody Task task) {
        return taskService.createTask(task);
    }

    /**
     * Actualiza tarea existente
     */
    @PutMapping("/update")
    public Task updateTask(@RequestBody Task task) {
        return taskService.updateTask(task);
    }

    /**
     * Actualiza estado
     */
    @PatchMapping("/update-status/{id}")
    public void updateTaskStatus(@PathVariable("id") Long id,
                                 @RequestParam("newStatus") Task.TaskStatus newStatus) {
        taskService.updateTaskStatus(id, newStatus);
    }

    /**
     * Elimina tarea
     */
    @DeleteMapping("/delete/{id}")
    public void deleteTask(@PathVariable("id") Long id) {
        taskService.deleteTask(id);
    }

    /**
     * Obtiene por ID
     */
    @GetMapping("/{id}")
    public Task getTask(@PathVariable("id") Long id) {
        return taskService.getTaskById(id);
    }

    /**
     * Obtiene todas
     */
    @GetMapping()
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * Busca por nombre
     */
    @GetMapping("/by-name")
    public List<Task> getTasksByName(@RequestParam("name") String name) {
        return taskService.getTaskByName(name);
    }

    /**
     * Obtiene de categoría
     */
    @GetMapping("/by-categoryid/{categoryId}")
    public List<Task> getTasksByCategoryId(@PathVariable("categoryId") Long categoryId) {
        return taskService.getTasksByCategoryId(categoryId);
    }

    /**
     * Cuenta en categoría
     */
    @GetMapping("/count-by-categoryid/{categoryId}")
    public Long getCountTasksByCategoryId(@PathVariable("categoryId") Long categoryId) {
        return taskService.getCountTasksByCategoryId(categoryId);
    }

    /**
     * Busca por estado
     */
    @GetMapping("/by-status")
    public List<Task> getTasksByStatus(@RequestParam("status") Task.TaskStatus status) {
        return taskService.getTasksByStatus(status);
    }

    /**
     * Busca por prioridad
     */
    @GetMapping("/by-priority")
    public List<Task> getTasksByPriority(@RequestParam("priority") Task.TaskPriority priority) {
        return taskService.getTasksByPriority(priority);
    }

    /**
     * Busca alta prioridad
     */
    @GetMapping("/high-priority")
    public List<Task> getHighPriorityTasks() {
        return taskService.getHighPriorityTasks();
    }

    /**
     * Cuenta por estado en categoría
     */
    @GetMapping("/count-by-categoryid-and-status/{categoryId}")
    public Long getTasksByCategoryIdAndStatus(@PathVariable("categoryId") Long categoryId,
                                              @RequestParam("status") Task.TaskStatus status) {
        return taskService.getTasksCountByCategoryAndStatus(categoryId, status);
    }

    /**
     * Lista recientes
     */
    @GetMapping("/latest")
    public List<Task> getLatestTasks() {
        return taskService.getLatestTasks();
    }

    /**
     * Busca por rango fechas
     */
    @GetMapping("/created-between-dates")
    public List<Task> getTasksCreatedBetweenDates(@RequestParam("from") LocalDateTime from,
                                                  @RequestParam("to") LocalDateTime to) {
        return taskService.getTasksCreatedBetween(from, to);
    }
}