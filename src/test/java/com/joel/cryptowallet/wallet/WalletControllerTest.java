package com.joel.cryptowallet.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joel.cryptowallet.wallet.controller.response.WalletBalanceResponse;
import com.joel.cryptowallet.wallet.controller.WalletController;
import com.joel.cryptowallet.wallet.controller.request.WalletCreationRequest;
import com.joel.cryptowallet.wallet.controller.response.WalletCreationResponse;
import com.joel.cryptowallet.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WalletControllerTest {
    private MockMvc sut;
    private WalletService walletService;
    private ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        walletService = mock(WalletService.class);
        sut = MockMvcBuilders.standaloneSetup(new WalletController(walletService)).build();
    }

    @Test
    void createWallet() throws Exception {
        String id = "sampleWalletId";
        String password = "sampleWalletPassword";
        String address = "sampleWalletAddress";
        String privateKey = "sampleWalletPrivateKey";
        when(walletService.createWallet(id, password)).thenReturn(
                WalletCreationResponse.builder()
                        .id(id)
                        .address(address)
                        .privateKey(privateKey)
                        .build()
        );

        sut.perform(
                post("/api/wallet")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(
                                mapper.writeValueAsString(
                                        WalletCreationRequest.builder()
                                                .id(id)
                                                .password(password)
                                                .build()
                                )
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sampleWalletId"))
                .andExpect(jsonPath("$.address").value("sampleWalletAddress"))
                .andExpect(jsonPath("$.privateKey").value("sampleWalletPrivateKey"));

        verify(walletService).createWallet(id, password);
    }

    @Test
    void getBalance() throws Exception {
        String walletAddress = "sampleWalletAddress";
        String balance = "0000000000";
        when(walletService.getBalance(walletAddress)).thenReturn(
                WalletBalanceResponse.builder()
                        .address(walletAddress)
                        .balance(balance)
                        .build()
        );

        sut.perform(
                get("/api/wallet/balance")
                        .queryParam("address", walletAddress)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(walletAddress))
                .andExpect(jsonPath("$.balance").value(balance));

        verify(walletService).getBalance(walletAddress);
    }
}