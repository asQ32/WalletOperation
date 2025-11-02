package ru.relex.DTO;

import ru.relex.entity.OperationType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * WalletBalanceDTO — DTO для ответа с информацией о выполненной операции по кошельку.
 * <p>
 *
 * @author andreyFocmhenko
 * @since 01.11.2025
 */
public record WalletBalanceDTO(
        UUID operationId,
        UUID walletId,
        OperationType operationType,
        BigDecimal amount,
        LocalDateTime createdAt
        ) {
}