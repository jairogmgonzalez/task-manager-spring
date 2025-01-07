package com.taskboard.taskboard.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskboard.taskboard.entities.User;
import com.taskboard.taskboard.repositories.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;

/**
 * Servicio para la gestión de Usuarios
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtiene usuario por ID
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los usuarios
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Cuenta total de usuarios
     */
    public long getCountUsers() {
        return userRepository.countUsers();
    }


    /**
     * Busca usuario por username
     */
    public User getUserByUsername(String username) {
        validateUsername(username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    /**
     * Busca usuario por email
     */
    public User getUserByEmail(String email) {
        validateEmail(email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
    }

    /**
     * Verifica si existe username
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Verifica si existe email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    /**
     * Busca usuarios por coincidencia en username
     */
    public List<User> getUsersByUsernameContains(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("La palabra clave no es válida");
        }
        return userRepository.searchByUsernameContaining(keyword.toLowerCase());
    }

    /**
     * Busca usuarios por dominio de email
     */
    public List<User> getUsersByEmailDomain(String domain) {
        if (domain == null || domain.trim().isEmpty() || !domain.startsWith("@")) {
            throw new IllegalArgumentException("Dominio no válido");
        }
        return userRepository.findUsersByEmailDomain(domain);
    }

    /**
     * Obtiene usuarios con username más largo
     */
    public List<User> getUsersWithLongestUsername() {
        return userRepository.findUsersWithLongestUsername();
    }


    /**
     * Obtiene usuarios con tableros
     */
    public List<User> getUsersWithBoards() {
        return userRepository.findUsersWithBoards();
    }

    /**
     * Obtiene usuarios sin tableros
     */
    public List<User> getUsersWithoutBoards() {
        return userRepository.findUsersWithoutBoards();
    }

    /**
     * Obtiene usuarios con más de X tableros
     */
    public List<String> getUsernamesWithMoreThanXBoards(Long minBoards) {
        if (minBoards < 0) {
            throw new IllegalArgumentException("El número mínimo de tableros no puede ser negativo");
        }
        return userRepository.findUsernamesWithMoreThanXBoards(minBoards);
    }


    /**
     * Crea un nuevo usuario
     */
    @Transactional
    public User createUser(User user) {
        if (user.getId() != null) {
            throw new IllegalArgumentException("Un nuevo usuario no debe tener ID");
        }
        validateUserFields(user);
        checkUniqueConstraints(user);
        return userRepository.save(user);
    }

    /**
     * Actualiza un usuario existente
     */
    @Transactional
    public User updateUser(User user) {
        User existingUser = getUserById(user.getId());
        validateUserFields(user);

        if (!user.getUsername().equals(existingUser.getUsername())) {
            checkUsernameUnique(user.getUsername());
        }

        if (!user.getEmail().equals(existingUser.getEmail())) {
            checkEmailUnique(user.getEmail());
        }

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        return userRepository.save(existingUser);
    }

    /**
     * Actualiza nombre de usuario
     */
    @Transactional
    public void updateUsername(Long id, String newUsername) {
        validateUsername(newUsername);
        getUserById(id);

        int updatedRows = userRepository.updateUsername(id, newUsername);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar el nombre del usuario");
        }
    }

    /**
     * Actualiza email de usuario
     */
    @Transactional
    public void updateEmail(Long id, String newEmail) {
        validateEmail(newEmail);
        getUserById(id);

        int updatedRows = userRepository.updateEmail(id, newEmail);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar el nombre del usuario");
        }
    }

    /**
     * Elimina un usuario
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = getUserById(id);
        if (!user.getBoards().isEmpty()) {
            throw new RuntimeException("No se puede eliminar un usuario con tableros");
        }
        userRepository.deleteById(id);
    }

    /**
     * MÉTODOS DE VALIDACIÓN
     */
    private void validateUserFields(User user) {
        validateUsername(user.getUsername());
        validateEmail(user.getEmail());
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El username no puede estar vacío");
        }
        if (username.length() < 3) {
            throw new IllegalArgumentException("El username debe tener al menos 3 caracteres");
        }
    }

    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }

    private void checkUniqueConstraints(User user) {
        if (existsByUsername(user.getUsername()) || existsByEmail(user.getEmail())) {
            throw new RuntimeException("Username o email ya existe");
        }
    }

    private void checkUsernameUnique(String username) {
        if (existsByUsername(username)) {
            throw new RuntimeException("Username ya existe");
        }
    }

    private void checkEmailUnique(String email) {
        if (existsByEmail(email)) {
            throw new RuntimeException("Email ya existe");
        }
    }
}