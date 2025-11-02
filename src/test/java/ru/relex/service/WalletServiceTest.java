package ru.relex.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.relex.DTO.RequestOperationDTO;
import ru.relex.DTO.ResponseOperationDTO;
import ru.relex.entity.OperationType;
import ru.relex.entity.Wallet;
import ru.relex.entity.WalletOperation;
import ru.relex.exception.InsufficientFundsException;
import ru.relex.exception.WalletNotFoundException;
import ru.relex.repository.WalletOperationsRepository;
import ru.relex.repository.WalletRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@DisplayName("Юнит-тесты сервиса WalletService")
class WalletServiceTest {

    private WalletRepository walletRepository;
    private WalletOperationsRepository walletOperationsRepository;
    private WalletService walletService;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        walletOperationsRepository = mock(WalletOperationsRepository.class);
        Executor syncExecutor = Runnable::run;
        walletService = new WalletService(walletRepository, walletOperationsRepository, syncExecutor);
        wallet = new Wallet();
        wallet.setId(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        wallet.setBalance(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("Пополнение кошелька увеличивает баланс")
    void shouldDepositMoney() {
        RequestOperationDTO request = new RequestOperationDTO(wallet.getId(), OperationType.DEPOSIT, 500.0);
        when(walletRepository.findByIdForUpdate(wallet.getId())).thenReturn(Optional.of(wallet));
        ResponseOperationDTO response = walletService.processOperation(request);
        assertThat(response.balance()).isEqualByComparingTo("1500");
        verify(walletRepository).save(wallet);
        verify(walletOperationsRepository).save(any(WalletOperation.class));
    }

    @Test
    @DisplayName("Снятие денег уменьшает баланс кошелька")
    void shouldWithdrawMoney() {
        RequestOperationDTO request = new RequestOperationDTO(wallet.getId(), OperationType.WITHDRAW, 200.0);
        when(walletRepository.findByIdForUpdate(wallet.getId())).thenReturn(Optional.of(wallet));
        ResponseOperationDTO response = walletService.processOperation(request);
        assertThat(response.balance()).isEqualByComparingTo("800");
        verify(walletRepository).save(wallet);
        verify(walletOperationsRepository).save(any(WalletOperation.class));
    }

    @Test
    @DisplayName("Ошибка при отсутствии кошелька")
    void shouldThrowWhenWalletNotFound() {
        UUID randomId = UUID.randomUUID();
        RequestOperationDTO request = new RequestOperationDTO(randomId, OperationType.DEPOSIT, 500.0);
        when(walletRepository.findByIdForUpdate(randomId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> walletService.processOperation(request))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessageContaining(randomId.toString());
    }

    @Test
    @DisplayName("Ошибка при попытке снять больше, чем баланс")
    void shouldThrowWhenInsufficientFunds() {
        wallet.setBalance(BigDecimal.valueOf(100));
        RequestOperationDTO request = new RequestOperationDTO(wallet.getId(), OperationType.WITHDRAW, 200.0);
        when(walletRepository.findByIdForUpdate(wallet.getId())).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> walletService.processOperation(request))
                .isInstanceOf(InsufficientFundsException.class)
                .hasMessageContaining(wallet.getId().toString());
    }

}
