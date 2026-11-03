
package com.growtrade.wallet.controller;

import com.growtrade.wallet.dto.AddMoneyRequest;
import com.growtrade.wallet.dto.WalletResponse;
import com.growtrade.wallet.dto.WalletTransactionResponse;
import com.growtrade.wallet.entity.WalletEntity;
import com.growtrade.wallet.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    // Create Wallet
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@RequestParam UUID userId) {

        WalletEntity wallet = walletService.createWallet(userId);

        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }

    // Get Wallet Balance
    @GetMapping("/{userId}")
    public WalletResponse getWallet(@PathVariable UUID userId) {
        return walletService.getWallet(userId);
    }

    // Add Demo Money
    @PostMapping("/deposit")
    public WalletResponse addMoney(
            @Valid @RequestBody AddMoneyRequest request
    ) {
        return walletService.addMoney(
                request.userId(),
                request.amount()
        );
    }

    @GetMapping("/{userId}/transactions")
    public List<WalletTransactionResponse> getTransactionHistory(
            @PathVariable UUID userId) {

        return walletService.getTransactionHistory(userId);
    }
}
