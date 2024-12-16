package com.zmo.springboot.zwallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zmo.springboot.zwallet.dto.WalletRequestDTO;
import com.zmo.springboot.zwallet.entity.OperationType;
import com.zmo.springboot.zwallet.servise.WalletServiceImpl;
import com.zmo.springboot.zwallet.utils.InsufficientFundsException;
import com.zmo.springboot.zwallet.utils.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class WalletControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private WalletServiceImpl walletServiceImpl;

    @InjectMocks
    private WalletController walletController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(walletController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getBalance() throws Exception {

        UUID id = UUID.randomUUID();
        Long balance = 100L;
        when(walletServiceImpl.getBalance(id)).thenReturn(balance);
        mockMvc.perform(get("/api/v1/wallets/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(balance));
    }

    @Test
    void update() throws Exception {
        UUID id = UUID.randomUUID();
        Long amount = 100L;
        OperationType operationType = OperationType.DEPOSIT;
        WalletRequestDTO walletRequestDTO = new WalletRequestDTO(id, operationType, amount);
        doNothing().when(walletServiceImpl).updateBalance(id, operationType, amount);
        mockMvc.perform(post("/api/v1/wallet").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void handleInsufficientFundsException() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new InsufficientFundsException("Insufficient Funds"))
                .when(walletServiceImpl).updateBalance(eq(id), any(), anyLong());

        WalletRequestDTO walletRequestDTO = new WalletRequestDTO(id, OperationType.WITHDRAW, 100L);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").value("Insufficient Funds"));
    }

    @Test
    void handleWalletNotFoundException() throws Exception {
        UUID id = UUID.randomUUID();
        when(walletServiceImpl.getBalance(id)).thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").value("Wallet not found"));
    }

    @Test
    public void handleInvalidJson() throws Exception {
        String invalidJson = "{ bad json }";
        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid JSON format"));
    }


}
