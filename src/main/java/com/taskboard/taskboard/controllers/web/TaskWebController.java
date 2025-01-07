package com.taskboard.taskboard.controllers.web;

import com.taskboard.taskboard.entities.Task;
import com.taskboard.taskboard.services.CategoryService;
import com.taskboard.taskboard.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Controlador Web para gestión de Tareas
 */
@Controller
@RequestMapping("/tasks")
public class TaskWebController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private CategoryService categoryService;


    /**
     * Muestra form creación
     */
    @GetMapping("/{boardId}/{categoryId}/add")
    public String add(@PathVariable("boardId") Long boardId,
                      @PathVariable("categoryId") Long categoryId,
                      Model model) {
        model.addAttribute("boardId", boardId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("task", new Task());
        return "task-add";
    }

    /**
     * Muestra form actualización
     */
    @GetMapping("/{boardId}/{categoryId}/update/{taskId}")
    public String updateCategory(@PathVariable("boardId") Long boardId,
                                 @PathVariable("categoryId") Long categoryId,
                                 @PathVariable("taskId") Long taskId,
                                 Model model) {
        model.addAttribute("boardId", boardId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("taskId", taskId);
        model.addAttribute("task", taskService.getTaskById(taskId));
        return "task-update";
    }


    /**
     * Crea nueva tarea
     */
    @PostMapping("/{boardId}/{categoryId}/add")
    public String addTask(@PathVariable("boardId") Long boardId,
                          @PathVariable("categoryId") Long categoryId,
                          @ModelAttribute Task task,
                          RedirectAttributes redirectAttributes) {
        try {
            task.setCategory(categoryService.getCategoryById(categoryId));
            taskService.createTask(task);
            return "redirect:/boards/" + boardId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks/" + boardId + "/" + categoryId + "/add";
        }
    }

    /**
     * Actualiza tarea
     */
    @PostMapping("/{boardId}/{categoryId}/update/{taskId}")
    public String updateTask(@PathVariable Long boardId,
                             @PathVariable Long categoryId,
                             @PathVariable Long taskId,
                             @RequestParam String name,
                             @RequestParam(required = false) String description,
                             @RequestParam Task.TaskStatus status,
                             @RequestParam Task.TaskPriority priority,
                             @RequestParam(required = false) LocalDateTime dueDate,
                             RedirectAttributes redirectAttributes) {
        try {
            Task existingTask = taskService.getTaskById(taskId);
            existingTask.setName(name);
            existingTask.setDescription(description);
            existingTask.setStatus(status);
            existingTask.setPriority(priority);
            existingTask.setDueDate(dueDate);

            taskService.updateTask(existingTask);
            redirectAttributes.addFlashAttribute("message", "Tarea actualizada correctamente");
            return "redirect:/boards/" + boardId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/tasks/" + boardId + "/" + categoryId + "/update/" + taskId;
        }
    }

    /**
     * Elimina tarea
     */
    @GetMapping("/{boardId}/delete/{taskId}")
    public String deleteTask(@PathVariable("boardId") Long boardId,
                             @PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
        return "redirect:/boards/" + boardId;
    }
}