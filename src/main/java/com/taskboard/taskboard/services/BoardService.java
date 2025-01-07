package com.taskboard.taskboard.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taskboard.taskboard.entities.Board;
import com.taskboard.taskboard.entities.Category;
import com.taskboard.taskboard.repositories.BoardRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio para la gestión de Tableros
 */
@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private UserService userService;

    /**
     * Obtiene tablero por ID
     */
    public Board getBoardById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(("Tablero no encontrado con ID: " + id)));
    }

    /**
     * Obtiene todos los tableros
     */
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    /**
     * Cuenta tableros con descripción
     */
    public Long countBoardsWithDescription() {
        return boardRepository.countBoardsWithDescription();
    }

    /**
     * Obtiene tableros de un usuario
     */
    public List<Board> getBoardsByUserId(Long userId) {
        userService.getUserById(userId);
        return boardRepository.findByUserId(userId);
    }

    /**
     * Busca tableros por nombre de usuario
     */
    public List<Board> getUserBoardsByName(Long userId, String name) {
        userService.getUserById(userId);
        validateName(name);
        return boardRepository.findUserBoardsByName(userId, name);
    }

    /**
     * Cuenta tableros de un usuario
     */
    public Long getBoardsCountByUserId(Long userId) {
        userService.getUserById(userId);
        return boardRepository.countBoardsByUserId(userId);
    }

    /**
     * Obtiene tableros con categorías de un usuario
     */
    public List<Board> getUserBoardsWithCategories(Long userId) {
        userService.getUserById(userId);
        return boardRepository.findUserBoardsWithCategories(userId);
    }

    /**
     * Obtiene tableros sin categorías de un usuario
     */
    public List<Board> getUserBoardsWithoutCategories(Long userId) {
        userService.getUserById(userId);
        return boardRepository.findUserBoardsWithoutCategories(userId);
    }

    /**
     * Obtiene tableros con mínimo de categorías
     */
    public List<Board> getBoardsWithMinCategories(Long minCategories) {
        if (minCategories < 0) {
            throw new IllegalArgumentException("El número mínimo de categorías no puede ser negativo");
        }
        return boardRepository.findBoardsWithMinCategories(minCategories);
    }

    /**
     * Obtiene tableros sin categorías
     */
    public List<Board> getBoardsWithoutCategories() {
        return boardRepository.findBoardsWithoutCategories();
    }

    /**
     * Obtiene tableros creados antes de fecha
     */
    public List<Board> getUsersBoardsCreatedBefore(Long userId, LocalDateTime date) {
        userService.getUserById(userId);
        return boardRepository.findUserBoardsCreatedBefore(userId, date);
    }

    /**
     * Obtiene tableros creados después de fecha
     */
    public List<Board> getUsersBoardsCreatedAfter(Long userId, LocalDateTime date) {
        userService.getUserById(userId);
        return boardRepository.findUserBoardsCreatedAfter(userId, date);
    }

    /**
     * Obtiene tableros creados después de fecha
     */
    public List<Board> getBoardsCreatedAfter(LocalDateTime date) {
        if (date == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }
        return boardRepository.findBoardsCreatedAfter(date);
    }

    /**
     * Obtiene tableros con más de X tareas
     */
    public List<Board> getBoardsWithMoreThanXTasks(Long minTasks) {
        if (minTasks < 0) {
            throw new IllegalArgumentException("El número mínimo de tareas no puede ser negativo");
        }
        return boardRepository.findBoardsWithMoreThanXTasks(minTasks);
    }

    /**
     * Crea nuevo tablero
     */
    @Transactional
    public Board createBoard(Board board) {
        if (board.getId() != null) {
            throw new IllegalArgumentException("Un nuevo tablero no debe tener ID");
        }
        validateBoard(board);
        userService.getUserById(board.getUser().getId());
        return boardRepository.save(board);
    }

    /**
     * Actualiza tablero existente
     */
    @Transactional
    public Board updateBoard(Board board) {
        Board existingBoard = getBoardById(board.getId());
        validateBoard(board);
        userService.getUserById(board.getUser().getId());

        if (board.getName() != null) existingBoard.setName(board.getName());
        if (board.getDescription() != null) existingBoard.setDescription(board.getDescription());
        if (board.getUser() != null) existingBoard.setUser(board.getUser());

        return boardRepository.save(existingBoard);
    }

    /**
     * Actualiza nombre de tablero
     */
    @Transactional
    public void updateBoardName(Long boardId, String newName) {
        validateName(newName);
        getBoardById(boardId);

        int updatedRows = boardRepository.updateBoardName(boardId, newName);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar el nombre");
        }
    }

    /**
     * Actualiza descripción de tablero
     */
    @Transactional
    public void updateBoardDescription(Long boardId, String newDescription) {
        validateDescription(newDescription);
        getBoardById(boardId);

        int updatedRows = boardRepository.updateBoardDescription(boardId, newDescription);
        if (updatedRows == 0) {
            throw new RuntimeException("No se pudo actualizar la descripción");
        }
    }

    /**
     * Elimina tablero
     */
    @Transactional
    public void deleteBoard(Long boardId) {
        boardRepository.deleteById(boardId);
    }

    /**
     * Añade categoría a tablero
     */
    @Transactional
    public Board addCategoryToBoard(Long boardId, Category category) {
        Board board = getBoardById(boardId);
        board.addCategory(category);
        return boardRepository.save(board);
    }

    /**
     * Elimina categoría de tablero
     */
    @Transactional
    public Board removeCategoryFromBoard(Long boardId, Category category) {
        Board board = getBoardById(boardId);
        board.removeCategory(category);
        return boardRepository.save(board);
    }

    /**
     * MÉTODOS DE VALIDACIÓN
     */
    private void validateBoard(Board board) {
        if (board.getName() == null || board.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (board.getUser() == null) {
            throw new IllegalArgumentException("Debe pertenecer a un usuario");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
    }

    private void validateDescription(String description) {
        if (description == null && description.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
    }
}