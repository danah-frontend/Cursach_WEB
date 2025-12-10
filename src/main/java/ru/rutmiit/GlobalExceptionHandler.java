package ru.rutmiit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.rutmiit.models.exceptions.ProductNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
        log.info("=== GLOBAL EXCEPTION HANDLER REGISTERED ===");
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleProductNotFound(ProductNotFoundException ex, Model model) {
        log.warn("GLOBAL HANDLER CAUGHT: ProductNotFoundException - {}", ex.getMessage());

        model.addAttribute("errorTitle", "Товар не найден");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        return "custom-error";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleRuntimeException(RuntimeException ex, Model model) {
        log.warn("GLOBAL HANDLER CAUGHT: RuntimeException - {}", ex.getMessage());

        String errorTitle;
        String errorCode;

        // Определяем тип ошибки по сообщению
        if (ex.getMessage().contains("нельзя удалить") ||
                ex.getMessage().contains("невозможно удалить") ||
                ex.getMessage().contains("активных заказов")) {
            errorTitle = "Невозможно удалить товар";
            errorCode = "400";
        } else if (ex.getMessage().contains("Недостаточно товара") ||
                ex.getMessage().contains("недостаточно товара")) {
            errorTitle = "Недостаточно товара";
            errorCode = "400";
        } else {
            errorTitle = "Ошибка операции";
            errorCode = "400";
        }

        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", errorCode);
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        return "custom-error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception ex, Model model) {
        log.error("GLOBAL HANDLER CAUGHT: Exception - {}", ex.getMessage(), ex);

        model.addAttribute("errorTitle", "Ошибка сервера");
        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
        model.addAttribute("errorCode", "500");
        model.addAttribute("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));

        return "custom-error";
    }
}