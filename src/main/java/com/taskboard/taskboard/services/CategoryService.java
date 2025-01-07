package com.taskboard.taskboard.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taskboard.taskboard.entities.Board;
import org.springframework.beans.factory.annotation.Autowired;

import com.taskboard.taskboard.entities.Category;
import com.taskboard.taskboard.entities.Task;
import com.taskboard.taskboard.repositories.CategoryRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de Categorías
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private BoardService boardService;
    @Autowired
    private UserService userService;

    /**
     * Obtiene categoría por ID
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(("Categoría no encontrada con ID: " + id)));
    }

    /**
     * Obtiene todas las categorías
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * Busca categorías por nombre
     */
    public List<Category> getCategoriesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        return categoryRepository.findByName(name);
    }

    /**
     * Obtiene categorías de un tablero
     */
    public List<Category> getCategoriesByBoardId(Long boardId) {
        boardService.getBoardById(boardId);
        return categoryRepository.findByBoardId(boardId);
    }

    /**
     * Busca categorías por nombre en tablero
     */
    public List<Category> getBoardCategoriesByName(Long boardId, String name) {
        boardService.getBoardById(boardId);
        validateName(name);
        return categoryRepository.findBoardCategoriesByName(boardId, name);
    }

    /**
     * Cuenta categorías en tablero
     */
    public Long getCategoriesCountByBoardId(Long boardId) {
        boardService.getBoardById(boardId);
        return categoryRepository.countCategoriesByBoardId(boardId);
    }

    /**
     * Obtiene mapa de tareas por categoría
     */
    public Map<Category, List<Task>> getTasksByCategory(Long boardId) {
        List<Category> categories = getCategoriesByBoardId(boardId);
        Map<Category, List<Task>> categoryTaskMap = new HashMap<>();
        for (Category category : categories) {
            categoryTaskMap.put(category, new ArrayList<>(category.getTasks()));
        }
        return categoryTaskMap;
    }

    /**
     * Cuenta tareas en categoría
     */
    public Long getTasksCountByCategoryId(Long categoryId) {
        getCategoryById(categoryId);
        return categoryRepository.countTasksInCategory(categoryId);
    }

    /**
     * Obtiene categorías con tareas de tablero
     */
    public List<Category> getBoardCategoriesWithTasks(Long boardId) {
        boardService.getBoardById(boardId);
        return categoryRepository.findBoardCategoriesWithTasks(boardId);
    }

    /**
     * Obtiene categorías sin tareas de tablero
     */
    public List<Category> getBoardCategoriesWithoutTasks(Long boardId) {
        boardService.getBoardById(boardId);
        return categoryRepository.findBoardCategoriesWithoutTasks(boardId);
    }

    /**
     * Obtiene todas las categorías con tareas
     */
    public List<Category> getCategoriesWithTasks() {
        return categoryRepository.findCategoriesWithTasks();
    }

    /**
     * Obtiene todas las categorías sin tareas
     */
    public List<Category> getCategoriesWithoutTasks() {
        return categoryRepository.findCategoriesWithoutTasks();
    }

    /**
     * Ordena categorías por número de tareas
     */
    public List<Category> getBoardCategoriesOrderByTaskCount(Long boardId) {
        boardService.getBoardById(boardId);
        return categoryRepository.findBoardCategoriesOrderByTaskCount(boardId);
    }

    /**
     * Cuenta categorías de usuario
     */
    public Long getCategoriesCountByUserId(Long userId) {
        userService.getUserById(userId);
        return categoryRepository.countCategoriesByUserId(userId);
    }

    /**
     * Obtiene categorías creadas después de fecha
     */
    public List<Category> getCategoriesCreatedAfter(LocalDateTime fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        return categoryRepository.findCategoriesCreatedAfter(fecha);
    }

    /**
     * Crea nueva categoría
     */
    @Transactional
    public Category createCategory(Category category) {
        if (category.getId() != null) {
            throw new IllegalArgumentException("Una nueva categoría no debe tener ID");
        }
        validateCategory(category);
        boardService.getBoardById(category.getBoard().getId());
        return categoryRepository.save(category);
    }

    /**
     * Actualiza categoría existente
     */
    @Transactional
    public Category updateCategory(Category category) {
        Category existingCategory = getCategoryById(category.getId());
        validateCategory(category);
        boardService.getBoardById(category.getBoard().getId());

        existingCategory.setName(category.getName());
        existingCategory.setBoard(category.getBoard());

        return categoryRepository.save(existingCategory);
    }

    /**
     * Actualiza nombre de categoría
     */
    @Transactional
    public void updateCategoryName(Long categoryId, String newName) {
        validateName(newName);
        Category existingCategory = getCategoryById(categoryId);

        int updatedRows = categoryRepository.updateCategoryName(categoryId, newName);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar el nombre");
        }
    }

    /**
     * Actualiza descripción de categoría
     */
    @Transactional
    public void updateCategoryDescription(Long categoryId, Long boardId, String newDescription) {
        validateDescription(newDescription);
        Category existingCategory = getCategoryById(categoryId);
        boardService.getBoardById(boardId);

        int updatedRows = categoryRepository.updateCategoryDescription(categoryId, boardId, newDescription);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar la descripción");
        }
    }

    /**
     * Elimina categoría
     */
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);

        categoryRepository.deleteById(categoryId);
    }

    /**
     * Añade tarea a categoría
     */
    @Transactional
    public Category addTask(Long categoryId, Task task) {
        Category category = getCategoryById(categoryId);
        category.addTask(task);
        return categoryRepository.save(category);
    }

    /**
     * Elimina tarea de categoría
     */
    @Transactional
    public Category removeTask(Long categoryId, Task task) {
        Category category = getCategoryById(categoryId);
        category.removeTask(task);
        return categoryRepository.save(category);
    }

    /**
     * VALIDACIONES
     */
    private void validateCategory(Category category) {
        validateName(category.getName());
        validateBoard(category.getBoard());
        validateDescription(category.getDescription());
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
    }

    private void validateBoard(Board board) {
        if (board == null) {
            throw new IllegalArgumentException("La categoría debe pertenecer a un tablero");
        }
    }

    private void validateDescription(String description) {
        if (description == null && description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía si se proporciona");
        }
    }
}
