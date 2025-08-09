package com.soduko;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.time.Duration;
import java.time.Instant;

@Controller
@SessionAttributes({"currentGame", "startTime", "playerName"})
public class SudokuController {

    @ModelAttribute("currentGame")
    public Game game() { return new Game(); }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("sudokuForm", new SudokuForm());
        return "index";
    }

    @PostMapping("/start")
    public String start(@RequestParam String playerName,
                        @ModelAttribute("currentGame") Game game,
                        Model model) {
        game.startGame();
        model.addAttribute("playerName", playerName);
        model.addAttribute("gameBoard", game.getBoard());
        model.addAttribute("sudokuForm", new SudokuForm());
        model.addAttribute("hasErrors", new boolean[9][9]);
        model.addAttribute("boardHasError", false);
        model.addAttribute("startTime", Instant.now());
        model.addAttribute("elapsedTime", 0);
        model.addAttribute("message", "Good luck, " + playerName + "!");
        return "sudoku";
    }

    @PostMapping("/submit")
    public String submitSolution(@ModelAttribute("currentGame") Game game,
                                 @ModelAttribute SudokuForm sudokuForm,
                                 @SessionAttribute("startTime") Instant startTime,
                                 @SessionAttribute("playerName") String playerName,
                                 Model model) {
        int[][] submitted = sudokuForm.getBoard();
        int[][] solution = game.getSolution();
        boolean[][] hasErrors = new boolean[9][9];
        boolean boardHasError = false;

        // Validate submitted against solution: empty (0) is allowed, treat as not wrong
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (submitted[i][j] != 0 && submitted[i][j] != solution[i][j]) {
                    hasErrors[i][j] = true;
                    boardHasError = true;
                }
            }
        }

        long timeTaken = Duration.between(startTime, Instant.now()).getSeconds();
        model.addAttribute("gameBoard", submitted);
        model.addAttribute("sudokuForm", sudokuForm);
        model.addAttribute("hasErrors", hasErrors);
        model.addAttribute("boardHasError", boardHasError);
        model.addAttribute("playerName", playerName);
        model.addAttribute("elapsedTime", timeTaken);

        if (!boardHasError) {
            model.addAttribute("message", "🎉 Congratulations " + playerName + "! Time: " + timeTaken + " seconds");
            return "success";
        } else {
            model.addAttribute("message", "❌ Some cells are wrong (highlighted in red).");
            return "sudoku";
        }
    }

    @PostMapping("/solution")
    public String showSolution(@ModelAttribute("currentGame") Game game,
                               @SessionAttribute("playerName") String playerName,
                               @SessionAttribute("startTime") Instant startTime,
                               Model model) {
        model.addAttribute("gameBoard", game.getSolution());
        model.addAttribute("sudokuForm", new SudokuForm());
        model.addAttribute("hasErrors", new boolean[9][9]);
        model.addAttribute("boardHasError", false);
        model.addAttribute("playerName", playerName);
        model.addAttribute("elapsedTime", Duration.between(startTime, Instant.now()).getSeconds());
        model.addAttribute("message", "Here is the solution.");
        return "sudoku";
    }

    @GetMapping("/new")
    public String newGame(SessionStatus status) {
        status.setComplete();
        return "redirect:/";
    }
}
