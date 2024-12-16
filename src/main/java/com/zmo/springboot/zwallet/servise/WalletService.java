package com.zmo.springboot.zwallet.servise;


import com.zmo.springboot.zwallet.entity.OperationType;

import java.util.UUID;

public interface WalletService {

    void updateBalance(UUID id, OperationType operationType, Long amount);

    Long getBalance(UUID id);

}
