package com.joel.cryptowallet.wallet.controller;

import com.joel.cryptowallet.wallet.controller.request.WalletCreationRequest;
import com.joel.cryptowallet.wallet.controller.response.WalletBalanceResponse;
import com.joel.cryptowallet.wallet.controller.response.WalletCreationResponse;
import com.joel.cryptowallet.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping
    public WalletCreationResponse createWallet(
            @RequestBody WalletCreationRequest request
    ) {
        return walletService.createWallet(request.id(), request.password());
    }

    @GetMapping("/balance")
    public WalletBalanceResponse getBalance(
            @RequestParam String address
    ) {
        return walletService.getBalance(address);
    }
}
