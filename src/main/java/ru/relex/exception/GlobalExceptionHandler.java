package ru.relex.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.relex.DTO.ErrorResponseDTO;

/**
 * {@code GlobalExceptionHandler} — глобальный обработчик исключений в приложении.
 * <p>
 * Этот класс перехватывает все исключения, возникающие во время выполнения REST-запросов,
 * и формирует стандартный ответ с описанием ошибки в формате {@link ErrorResponseDTO}.
 * <br>
 * Он обеспечивает централизованную обработку ошибок и преобразование технических исключений
 * в понятные пользователю сообщения с корректными HTTP-кодами.
 * </p>
 *
 * <p>
 * Обрабатываемые типы исключений:
 * <ul>
 *   <li>{@link WalletNotFoundException} — кошелёк не найден (HTTP 404)</li>
 *   <li>{@link InsufficientFundsException} — недостаточно средств (HTTP 400)</li>
 *   <li>{@link InvalidOperationTypeException} — некорректный тип операции (HTTP 400)</li>
 *   <li>{@link WalletAlreadyExistsException} — кошелёк уже существует (HTTP 409)</li>
 *   <li>{@link DatabaseOperationException} — ошибка базы данных (HTTP 500)</li>
 *   <li>{@link java.lang.Exception} — все прочие необработанные исключения (HTTP 500)</li>
 * </ul>
 * </p>
 *
 * <p>
 * Используется аннотация {@link org.springframework.web.bind.annotation.RestControllerAdvice},
 * которая делает этот класс доступным для всех контроллеров приложения.
 * </p>
 *
 * @author andreyFomchenko
 * @since 02.11.2025
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleWalletNotFoundException(WalletNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND
                ).body(ErrorResponseDTO.of("Wallet not found", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientFundsException(InsufficientFundsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of("Insufficient funds", ex.getMessage()));
    }

    @ExceptionHandler(InvalidOperationTypeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidOperationTypeException(InvalidOperationTypeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of("Invalid operation type", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponseDTO> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDTO.of("Insufficient balance", ex.getMessage()));
    }

    @ExceptionHandler(WalletAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDTO> handleWalletAlreadyExistsException(WalletAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponseDTO.of("Wallet already exists", ex.getMessage()));
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDatabaseOperationException(DatabaseOperationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of("Database operation failed", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponseDTO.of("Internal server error", ex.getMessage()));
    }
}
