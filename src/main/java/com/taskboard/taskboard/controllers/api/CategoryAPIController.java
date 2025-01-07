package com.taskboard.taskboard.controllers.api;

import com.taskboard.taskboard.entities.Category;
import com.taskboard.taskboard.entities.Task;
import com.taskboard.taskboard.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de Categorías
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryAPIController {

    @Autowired
    private CategoryService categoryService;

    /**
     * Crea nueva categoría
     */
    @PostMapping("/add")
    public Category addCategory(@RequestBody Category category) {
        return categoryService.createCategory(category);
    }

    /**
     * Actualiza categoría existente
     */
    @PutMapping("/update")
    public Category updateCategory(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    /**
     * Actualiza nombre de categoría
     */
    @PatchMapping("/update-name/{id}")
    public void updateCategoryName(@PathVariable("id") Long id, @RequestParam("newName") String newName) {
        categoryService.updateCategoryName(id, newName);
    }

    /**
     * Elimina categoría
     */
    @DeleteMapping("/delete/{id}")
    public void removeCategory(@PathVariable("id") Long id) {
        categoryService.deleteCategory(id);
    }

    /**
     * Añade tarea a categoría
     */
    @PostMapping("/{id}/tasks/add")
    public Category addTask(@PathVariable("id") Long id, @RequestBody Task task) {
        return categoryService.addTask(id, task);
    }

    /**
     * Elimina tarea de categoría
     */
    @DeleteMapping("/{id}/tasks/remove")
    public Category removeTask(@PathVariable("id") Long id, @RequestBody Task task) {
        return categoryService.removeTask(id, task);
    }

    /**
     * Obtiene categoría por ID
     */
    @GetMapping("/{id}")
    public Category getCategory(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * Obtiene todas las categorías
     */
    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    /**
     * Busca categorías por nombre
     */
    @GetMapping("/by-name")
    public List<Category> getCategoriesByName(@RequestParam("name") String name) {
        return categoryService.getCategoriesByName(name);
    }

    /**
     * Obtiene categorías de tablero
     */
    @GetMapping("/by-boardid/{boardId}")
    public List<Category> getCategoriesByBoardId(@PathVariable("boardId") Long boardId) {
        return categoryService.getCategoriesByBoardId(boardId);
    }

    /**
     * Cuenta categorías de tablero
     */
    @GetMapping("/count-by-board/{boardId}")
    public Long getCategoriesCountByBoardId(@PathVariable("boardId") Long boardId) {
        return categoryService.getCategoriesCountByBoardId(boardId);
    }

    /**
     * Obtiene categorías con tareas
     */
    @GetMapping("/with-tasks")
    public List<Category> getCategoriesWithTasks() {
        return categoryService.getCategoriesWithTasks();
    }

    /**
     * Obtiene categorías sin tareas
     */
    @GetMapping("/without-tasks")
    public List<Category> getCategoriesWithoutTasks() {
        return categoryService.getCategoriesWithoutTasks();
    }

    /**
     * Obtiene categorías por fecha
     */
    @GetMapping("/created-after")
    public List<Category> getCreatedAfter(@RequestParam("date") LocalDateTime date) {
        return categoryService.getCategoriesCreatedAfter(date);
    }
}