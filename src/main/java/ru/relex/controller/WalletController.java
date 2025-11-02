package ru.relex.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.relex.DTO.RequestOperationDTO;
import ru.relex.DTO.ResponseOperationDTO;
import ru.relex.DTO.WalletBalanceDTO;
import ru.relex.service.WalletService;

import java.util.List;
import java.util.UUID;

/**
 * WalletController — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author Пользователь
 * @since 02.11.2025
 */
@Controller
@RequestMapping("api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/operation")
    public ResponseEntity<ResponseOperationDTO> createOperation(@RequestBody RequestOperationDTO request) {
        ResponseOperationDTO response = walletService.processOperation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{walletId}/operations")
    public ResponseEntity<List<WalletBalanceDTO>> getWalletOperations(@PathVariable("walletId") UUID walletId) {
        List<WalletBalanceDTO> response = walletService.getWalletOperations(walletId);
        return ResponseEntity.ok(response);
    }
}
