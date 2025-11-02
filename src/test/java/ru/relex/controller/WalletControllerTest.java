package ru.relex.controller;

import ru.relex.DTO.RequestOperationDTO;
import ru.relex.DTO.ResponseOperationDTO;
import ru.relex.DTO.WalletBalanceDTO;
import ru.relex.entity.OperationType;
import ru.relex.exception.InsufficientFundsException;
import ru.relex.exception.WalletNotFoundException;
import ru.relex.service.WalletService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Успешное создание операции по кошельку")
    void shouldCreateOperationSuccessfully() throws Exception {
        RequestOperationDTO request = new RequestOperationDTO(
                UUID.randomUUID(), OperationType.DEPOSIT,100.0
        );
        ResponseOperationDTO response = new ResponseOperationDTO(request.walletId(), BigDecimal.valueOf(100));

        when(walletService.processOperation(any(RequestOperationDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/wallets/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(request.walletId().toString()))
                .andExpect(jsonPath("$.balance").value(100));
    }

    @Test
    @DisplayName("Попытка создания операции с недостаточными средствами")
    void shouldReturnBadRequestWhenInsufficientFunds() throws Exception {
        RequestOperationDTO request = new RequestOperationDTO(
                UUID.randomUUID(), OperationType.WITHDRAW,1000.0
        );

        doThrow(new InsufficientFundsException(request.walletId().toString()))
                .when(walletService).processOperation(any(RequestOperationDTO.class));

        mockMvc.perform(post("/api/v1/wallets/operation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Insufficient funds"));
    }

    @Test
    @DisplayName("Получение операций кошелька успешно")
    void shouldGetWalletOperationsSuccessfully() throws Exception {
        UUID walletId = UUID.randomUUID();
        WalletBalanceDTO op = new WalletBalanceDTO(UUID.randomUUID(), walletId,
                null, BigDecimal.valueOf(100), null);
        when(walletService.getWalletOperations(walletId)).thenReturn(List.of(op));

        mockMvc.perform(get("/api/v1/wallets/{walletId}/operations", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].walletId").value(walletId.toString()))
                .andExpect(jsonPath("$[0].amount").value(100));
    }

    @Test
    @DisplayName("Попытка получения операций для несуществующего кошелька")
    void shouldReturnNotFoundForMissingWallet() throws Exception {
        UUID walletId = UUID.randomUUID();

        doThrow(new WalletNotFoundException(walletId.toString()))
                .when(walletService).getWalletOperations(walletId);

        mockMvc.perform(get("/api/v1/wallets/{walletId}/operations", walletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Wallet not found"));
    }
}
