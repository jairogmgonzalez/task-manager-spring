package com.taskboard.taskboard.controllers.web;

import com.taskboard.taskboard.entities.User;
import com.taskboard.taskboard.services.BoardService;
import com.taskboard.taskboard.services.CategoryService;
import com.taskboard.taskboard.services.TaskService;
import com.taskboard.taskboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador Web para gestión de Usuarios
 */
@Controller
@RequestMapping("/user")
public class UserWebController {

    @Autowired
    private UserService userService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private TaskService taskService;

    private static final Long USER_ID = 1L;

    /**
     * Muestra perfil de usuario con estadísticas
     */
    @GetMapping("/{id}")
    public String showProfile(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        Long boardCount = boardService.getBoardsCountByUserId(id);
        Long categoryCount = categoryService.getCategoriesCountByUserId(id);
        Long taskCount = taskService.getTasksCountByUserId(id);

        model.addAttribute("user", user);
        model.addAttribute("boardCount", boardCount);
        model.addAttribute("categoryCount", categoryCount);
        model.addAttribute("taskCount", taskCount);

        return "user";
    }

    /**
     * Actualiza datos de perfil de usuario
     */
    @PostMapping("/{id}/update")
    public String updateProfile(@PathVariable("id") Long id,
                                @RequestParam("newUsername") String newUsername,
                                @RequestParam("newEmail") String newEmail,
                                RedirectAttributes redirectAttributes) {
        try {
            userService.updateUsername(id, newUsername);
            userService.updateEmail(id, newEmail);
            redirectAttributes.addFlashAttribute("message", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/user/" + id;
    }
}
