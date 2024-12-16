package com.zmo.springboot.zwallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class WalletResponseDTO {

    private UUID id;
    private Long balance;
}
