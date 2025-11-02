package ru.relex.DTO;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * ResponseOperationDTO — DTO для ответа с текущим балансом кошелька.
 *
 * @author andreyFomchenko
 * @since 01.11.2025
 */
public record ResponseOperationDTO(
        UUID walletId,
        BigDecimal balance
) {
}