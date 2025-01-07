package com.taskboard.taskboard.controllers.api;

import com.taskboard.taskboard.entities.Board;
import com.taskboard.taskboard.entities.Category;
import com.taskboard.taskboard.services.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador REST para gestión de Tableros
 */
@RestController
@RequestMapping("/api/boards")
public class BoardAPIController {

    @Autowired
    private BoardService boardService;

    /**
     * Crea nuevo tablero
     */
    @PostMapping("/add")
    public Board addBoard(@RequestBody Board board) {
        return boardService.createBoard(board);
    }

    /**
     * Actualiza tablero existente
     */
    @PutMapping("/update")
    public Board updateBoard(@RequestBody Board board) {
        return boardService.updateBoard(board);
    }

    /**
     * Actualiza nombre de tablero
     */
    @PatchMapping("/update-name/{id}")
    public void updateBoardName(@PathVariable("id") Long id, @RequestParam("newName") String newName) {
        boardService.updateBoardName(id, newName);
    }

    /**
     * Elimina un tablero
     */
    @DeleteMapping("/delete/{id}")
    public void deleteBoard(@PathVariable("id") Long id) {
        boardService.deleteBoard(id);
    }

    /**
     * Añade categoría a tablero
     */
    @PostMapping("/{id}/categories/add")
    public Board addCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        return boardService.addCategoryToBoard(id, category);
    }

    /**
     * Elimina categoría de tablero
     */
    @DeleteMapping("/{id}/categories/remove")
    public Board removeCategory(@PathVariable("id") Long id, @RequestBody Category category) {
        return boardService.removeCategoryFromBoard(id, category);
    }

    /**
     * Obtiene tablero por ID
     */
    @GetMapping("/{id}")
    public Board getBoard(@PathVariable("id") Long id) {
        return boardService.getBoardById(id);
    }

    /**
     * Obtiene todos los tableros
     */
    @GetMapping
    public List<Board> getAllBoards() {
        return boardService.getAllBoards();
    }

    /**
     * Obtiene tableros de usuario
     */
    @GetMapping("/by-userid/{userId}")
    public List<Board> getBoardsByUserId(@PathVariable("userId") Long userId) {
        return boardService.getBoardsByUserId(userId);
    }

    /**
     * Cuenta tableros de usuario
     */
    @GetMapping("count-by-userid/{id}")
    public Long getBoardsCountByUserid(@PathVariable("id") Long userId) {
        return boardService.getBoardsCountByUserId(userId);
    }

    /**
     * Obtiene tableros por fecha
     */
    @GetMapping("/created-after")
    public List<Board> getCreatedAfter(@RequestParam("date") LocalDateTime date) {
        return boardService.getBoardsCreatedAfter(date);
    }

    /**
     * Cuenta tableros con descripción
     */
    @GetMapping("/count-with-desc")
    public Long getBoardsCountWithDesc() {
        return boardService.countBoardsWithDescription();
    }

    /**
     * Filtra por mínimo de categorías
     */
    @GetMapping("/with-min-categories")
    public List<Board> getBoardsWithMinCategories(@RequestParam("minCategories") Long minCategories) {
        return boardService.getBoardsWithMinCategories(minCategories);
    }

    /**
     * Filtra por mínimo de tareas
     */
    @GetMapping("/more-than-tasks")
    public List<Board> getMoreThanTasks(@RequestParam("minTasks") Long minTasks) {
        return boardService.getBoardsWithMoreThanXTasks(minTasks);
    }

    /**
     * Obtiene tableros sin categorías
     */
    @GetMapping("/without-categories")
    public List<Board> getBoardsWithoutCategories() {
        return boardService.getBoardsWithoutCategories();
    }
}
