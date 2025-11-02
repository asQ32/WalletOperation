package ru.relex.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.relex.DTO.RequestOperationDTO;
import ru.relex.DTO.ResponseOperationDTO;
import ru.relex.DTO.WalletBalanceDTO;
import ru.relex.entity.OperationType;
import ru.relex.entity.Wallet;
import ru.relex.entity.WalletOperation;
import ru.relex.exception.InsufficientFundsException;
import ru.relex.exception.WalletNotFoundException;
import ru.relex.repository.WalletOperationsRepository;
import ru.relex.repository.WalletRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * WalletService — сервис для управления кошельками и операциями.
 * <p>
 * Обрабатывает создание операций, обновление баланса
 * и получение истории операций кошелька.
 * Асинхронное обновление баланса выполняется через ExecutorService.
 * </p>
 *
 * @author andreyFomchenko
 * @since 02.11.2025
 */
@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletOperationsRepository walletOperationsRepository;
    private final Executor asyncExecutor;


    @Transactional
    @Async("asyncExecutor")
    public ResponseOperationDTO processOperation(RequestOperationDTO request) {
        Wallet wallet = walletRepository.findByIdForUpdate(request.walletId())
                .orElseThrow(() -> new WalletNotFoundException(request.walletId().toString()));

        BigDecimal amount = BigDecimal.valueOf(request.amount());

        if (request.operationType() == OperationType.WITHDRAW && wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(request.walletId().toString());
        }

        BigDecimal newBalance = request.operationType() == OperationType.DEPOSIT
                ? wallet.getBalance().add(amount)
                : wallet.getBalance().subtract(amount);

        wallet.setBalance(newBalance);
        walletRepository.save(wallet);

        WalletOperation operation = new WalletOperation(
                UUID.randomUUID(),
                wallet,
                request.operationType(),
                amount,
                LocalDateTime.now()
        );

        CompletableFuture.runAsync(() -> walletOperationsRepository.save(operation), asyncExecutor);

        return new ResponseOperationDTO(wallet.getId(), wallet.getBalance());
    }


    @Transactional
    public List<WalletBalanceDTO> getWalletOperations(UUID walletId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId.toString()));

        return walletOperationsRepository.findByWalletId(walletId).stream()
                .map(op -> new WalletBalanceDTO(
                        op.getId(),
                        wallet.getId(),
                        op.getOperationType(),
                        op.getAmount(),
                        op.getCreatedAt()
                ))
                .toList();
    }

}
