package com.zmo.springboot.zwallet.dto;


import com.zmo.springboot.zwallet.entity.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletRequestDTO  {

    private UUID walletId;
    private OperationType operationType;
    private Long amount;

}

