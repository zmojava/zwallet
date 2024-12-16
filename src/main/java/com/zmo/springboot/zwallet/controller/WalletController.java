package com.zmo.springboot.zwallet.controller;


import com.zmo.springboot.zwallet.dto.WalletRequestDTO;
import com.zmo.springboot.zwallet.dto.WalletResponseDTO;
import com.zmo.springboot.zwallet.servise.WalletService;
import com.zmo.springboot.zwallet.servise.WalletServiceImpl;
import com.zmo.springboot.zwallet.utils.InsufficientFundsException;
import com.zmo.springboot.zwallet.utils.WalletNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class WalletController {

    private final WalletService walletServiceImpl;

    public WalletController(WalletServiceImpl walletServiceImpl) {
        this.walletServiceImpl = walletServiceImpl;
    }

    @GetMapping("/wallets/{id}")
    public ResponseEntity<WalletResponseDTO> getBalance(@PathVariable("id") UUID id) {
        log.info("Получен запрос на получение баланса для кошелька с ID: {}", id);
        Long balance = walletServiceImpl.getBalance(id);
        log.info("Баланс кошелька с ID {}: {}", id, balance);
        return ResponseEntity.ok(new WalletResponseDTO(id, balance));
    }

    @PostMapping("/wallet")
    public ResponseEntity<String> update(@RequestBody WalletRequestDTO walletRequestDTO) {
        log.info("Получен запрос на обновление кошелька: {}", walletRequestDTO);
        walletServiceImpl.updateBalance(
                walletRequestDTO.getWalletId(),
                walletRequestDTO.getOperationType(),
                walletRequestDTO.getAmount()
        );
        log.info("Кошелек с ID {} успешно обновлен", walletRequestDTO.getWalletId());
        return ResponseEntity.ok("Кошелек успешно обновлен");
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<String> handleInsufficientFunds(InsufficientFundsException ex) {
        log.error("Ошибка: недостаточно средств. Детали: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Insufficient Funds");
    }

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<String> handleWalletNotFoundException(WalletNotFoundException ex) {
        log.error("Ошибка: кошелек не найден. Детали: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wallet not found");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("Ошибка: некорректный формат JSON. Детали: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format");
    }
}
