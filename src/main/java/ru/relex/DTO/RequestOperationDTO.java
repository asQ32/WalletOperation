package ru.relex.DTO;

import ru.relex.entity.OperationType;

import java.util.UUID;

/**
 * WalletOperationDTO — DTO для запроса на выполнение операции с кошельком (пополнение или списание).
 *
 * @author andreyFomchenko
 * @since 01.11.2025
 */
public record RequestOperationDTO(
        UUID walletId,
        OperationType operationType,
        double amount
) {
}