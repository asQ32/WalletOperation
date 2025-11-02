package ru.relex.DTO;

import java.time.LocalDateTime;

/**
 * ErrorResponseDTO — DTO-класс, представляющий стандартную структуру ответа об ошибке,
 * возвращаемого при обработке исключений в приложении.
 *
 * <p>Содержит краткое описание ошибки, сообщение для пользователя и временную метку
 * момента возникновения ошибки.</p>
 *
 * <p>Используется для формирования единообразных ответов на ошибки.</p>
 *
 * @param error     краткое описание типа ошибки
 * @param message   сообщение, поясняющее причину ошибки
 * @param timestamp время возникновения ошибки
 *
 * @author andreyFomchenko
 * @since 02.11.2025
 */
public record ErrorResponseDTO(
        String error,
        String message,
        LocalDateTime timestamp
) {
    public static ErrorResponseDTO of(String error, String message) {
        return new ErrorResponseDTO(error, message, LocalDateTime.now());
    }
}