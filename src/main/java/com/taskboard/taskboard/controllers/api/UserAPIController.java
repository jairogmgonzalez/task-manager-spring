package com.taskboard.taskboard.controllers.api;

import com.taskboard.taskboard.entities.User;
import com.taskboard.taskboard.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de Usuarios
 */
@RestController
@RequestMapping("/api/users")
public class UserAPIController {

    @Autowired
    private UserService userService;

    /**
     * Crea un nuevo usuario
     */
    @PostMapping("/add")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Actualiza un usuario existente
     */
    @PutMapping("/update")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Actualiza el username de un usuario
     */
    @PatchMapping("/update-username/{id}")
    public void updateUserName(@PathVariable("id") Long id, @RequestParam String newUsername) {
        userService.updateUsername(id, newUsername);
    }

    /**
     * Elimina un usuario
     */
    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
    }

    /**
     * Obtiene un usuario por ID
     */
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    /**
     * Obtiene todos los usuarios
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Obtiene usuario por username
     */
    @GetMapping("/by-username")
    public User getUserByUsername(@RequestParam("username") String username) {
        return userService.getUserByUsername(username);
    }

    /**
     * Obtiene usuario por email
     */
    @GetMapping("/by-email")
    public User getUserByEmail(@RequestParam("email") String email) {
        return userService.getUserByEmail(email);
    }

    /**
     * Obtiene total de usuarios
     */
    @GetMapping("/count")
    public Long getCountUsers() {
        return userService.getCountUsers();
    }

    /**
     * Busca usuarios por coincidencia en username
     */
    @GetMapping("/search")
    public List<User> getUsersByUsernameContains(@RequestParam("keyword") String keyword) {
        return userService.getUsersByUsernameContains(keyword);
    }

    /**
     * Filtra usuarios con/sin tableros
     */
    @GetMapping("/filtered")
    public List<User> getUsersFiltered(@RequestParam("hasBoards") boolean hasBoards) {
        return hasBoards ? userService.getUsersWithBoards() : userService.getUsersWithoutBoards();
    }

    /**
     * Obtiene nombreusuarios con más de X tableros
     */
    @GetMapping("/with-more-than/{count}")
    public List<String> getUsersnamesWithMoreThanXBoards(@PathVariable("count") long count) {
        return userService.getUsernamesWithMoreThanXBoards(count);
    }

    /**
     * Obtiene usuarios por dominio de email
     */
    @GetMapping("/by-email/{domain}")
    public List<User> getUsersByEmailDomain(@PathVariable("domain") String domain) {
        return userService.getUsersByEmailDomain(domain);
    }

    /**
     * Obtiene usuarios con username más largo
     */
    @GetMapping("/longest-username")
    public List<User> getUsersWithLongestUsername() {
        return userService.getUsersWithLongestUsername();
    }
}