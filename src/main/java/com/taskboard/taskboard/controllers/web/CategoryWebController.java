package com.taskboard.taskboard.controllers.web;

import com.taskboard.taskboard.entities.Board;
import com.taskboard.taskboard.entities.Category;
import com.taskboard.taskboard.services.BoardService;
import com.taskboard.taskboard.services.CategoryService;
import com.taskboard.taskboard.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Web para gestión de Categorías
 */
@Controller
@RequestMapping("/categories")
public class CategoryWebController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private TaskService taskService;

    /**
     * Muestra formulario de creación
     */
    @GetMapping("/{boardId}/add")
    public String addCategory(@PathVariable("boardId") Long boardId, Model model) {
        Board board = boardService.getBoardById(boardId);
        model.addAttribute("board", board);
        return "category-add";
    }

    /**
     * Muestra formulario de actualización
     */
    @GetMapping("/{boardId}/update/{categoryId}")
    public String updateCategory(@PathVariable("boardId") Long boardId,
                                 @PathVariable("categoryId") Long categoryId,
                                 Model model) {
        Board board = boardService.getBoardById(boardId);
        Category category = categoryService.getCategoryById(categoryId);

        model.addAttribute("board", board);
        model.addAttribute("category", category);
        model.addAttribute("taskCount", categoryService.getTasksCountByCategoryId(categoryId));

        return "category-update";
    }

    /**
     * Crea nueva categoría
     */
    @PostMapping("/{boardId}/add")
    public String addCategory(@PathVariable("boardId") Long boardId,
                              @RequestParam("name") String name,
                              @RequestParam(value = "description", required = false) String description,
                              RedirectAttributes redirectAttributes) {
        try {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            category.setBoard(boardService.getBoardById(boardId));

            categoryService.createCategory(category);
            return "redirect:/boards/" + boardId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/categories/" + boardId + "/add";
        }
    }

    /**
     * Actualiza categoría existente
     */
    @PostMapping("/{boardId}/update/{categoryId}")
    public String updateCategory(@PathVariable("boardId") Long boardId,
                                 @PathVariable("categoryId") Long categoryId,
                                 @RequestParam("newName") String newName,
                                 @RequestParam("newDescription") String newDescription,
                                 RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategoryName(categoryId, newName);
            categoryService.updateCategoryDescription(categoryId, boardId, newDescription);
            redirectAttributes.addFlashAttribute("message", "Categoría actualizada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/categories/" + boardId + "/update/" + categoryId;
    }

    /**
     * Elimina categoría
     */
    @GetMapping("/{boardId}/delete/{categoryId}")
    public String deleteBoard(@PathVariable("boardId") Long boardId,
                              @PathVariable("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return "redirect:/boards/" + boardId;
    }
}

