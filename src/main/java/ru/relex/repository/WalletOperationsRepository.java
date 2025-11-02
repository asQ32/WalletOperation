package ru.relex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.relex.entity.WalletOperation;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий для работы с сущностью {@link WalletOperation}.
 * <p>
 * Предоставляет стандартные CRUD-операции и базовые методы выборки.
 * </p>
 *
 * @author andreyFomchenko
 * @since 02.11.2025
 */
public interface WalletOperationsRepository extends JpaRepository<WalletOperation, UUID> {

    /**
     * Находит все операции по идентификатору кошелька.
     *
     * @param walletId идентификатор кошелька
     * @return список операций
     */
    List<WalletOperation> findByWalletId(UUID walletId);
}
