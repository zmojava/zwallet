package com.zmo.springboot.zwallet.servise;


import com.zmo.springboot.zwallet.entity.OperationType;
import com.zmo.springboot.zwallet.entity.Wallet;
import com.zmo.springboot.zwallet.repository.WalletRepository;
import com.zmo.springboot.zwallet.utils.InsufficientFundsException;
import com.zmo.springboot.zwallet.utils.WalletNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    public WalletServiceImpl(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Override
    @Transactional
    @CacheEvict(value = "WalletServiceImpl::getBalance", key = "#id")
    public void updateBalance(UUID id, OperationType operationType, Long amount) {
        log.info("Попытка обновления баланса для кошелька с ID: {}, операция: {}, сумма: {}", id, operationType, amount);

        if (amount == null || amount <= 0) {
            log.error("Сумма для обновления должна быть положительным числом, передано: {}", amount);
            throw new IllegalArgumentException("Сумма должна быть положительным числом");
        }

        Wallet wallet = walletRepository.findById(id).orElseThrow(() -> {
            log.error("Кошелек с ID {} не найден", id);
            return new WalletNotFoundException("Кошелек не найден");
        });

        if (operationType == OperationType.WITHDRAW && wallet.getBalance() < amount) {
            log.error("Недостаточно средств для снятия. Баланс: {}, запрашиваемая сумма: {}", wallet.getBalance(), amount);
            throw new InsufficientFundsException("Недостаточно средств");
        }

        if (operationType == OperationType.DEPOSIT) {
            wallet.setBalance(wallet.getBalance() + amount);
        } else if (operationType == OperationType.WITHDRAW) {
            wallet.setBalance(wallet.getBalance() - amount);
        }

        walletRepository.save(wallet);
        log.info("Кошелек с ID {} успешно обновлен", id);
    }

    @Override
    @Transactional
    @Cacheable(value = "WalletServiceImpl::getBalance", key = "#id")
    public Long getBalance(UUID id) {
        log.info("Запрос баланса для кошелька с ID: {}", id);

        return walletRepository.findById(id)
                .map(Wallet::getBalance)
                .orElseThrow(() -> {
                    log.error("Кошелек с ID {} не найден", id);
                    return new WalletNotFoundException("Кошелек не найден");
                });
    }
}
