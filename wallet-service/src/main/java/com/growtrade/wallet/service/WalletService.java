
package com.growtrade.wallet.service;

import com.growtrade.wallet.dto.WalletResponse;
import com.growtrade.wallet.dto.WalletTransactionResponse;
import com.growtrade.wallet.entity.WalletEntity;
import com.growtrade.wallet.entity.WalletTransactionEntity;
import com.growtrade.wallet.event.TradeExecutedEvent;
import com.growtrade.wallet.repository.WalletRepository;
import com.growtrade.wallet.repository.WalletTransactionRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private static final Logger log =
            LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public WalletEntity createWallet(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException(
                    "Wallet already exists for this user"
            );
        }

        Instant now = Instant.now();

        WalletEntity wallet = new WalletEntity(
                UUID.randomUUID(),
                userId,
                now
        );

        return walletRepository.save(wallet);
    }

    @Transactional(readOnly = true)
    public WalletResponse getWallet(UUID userId) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        WalletEntity wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Wallet not found for user: " + userId
                ));

        return toResponse(wallet);
    }

    @Transactional
    public WalletResponse addMoney(UUID userId, BigDecimal amount) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (amount == null || amount.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException(
                    "Minimum deposit amount is 0.01"
            );
        }

        WalletEntity wallet = walletRepository
                .findByUserIdForUpdate(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Wallet not found for user: " + userId
                ));

        BigDecimal newBalance = wallet.getBalance().add(amount);

        Instant now = Instant.now();

        wallet.updateBalance(newBalance, now);
        walletRepository.save(wallet);

        WalletTransactionEntity transaction =
                new WalletTransactionEntity(
                        UUID.randomUUID(),
                        wallet.getId(),
                        "DEPOSIT",
                        amount,
                        null,
                        "Demo wallet deposit",
                        now
                );

        transactionRepository.save(transaction);

        return toResponse(wallet);
    }

    // Process BUY / SELL event received from Kafka
    @Transactional
    public void processTradeExecutedEvent(TradeExecutedEvent event) {

        if (event == null || event.executionId() == null
                || event.userId() == null) {
            throw new IllegalArgumentException("Invalid trade event");
        }

        if (!"FILLED".equalsIgnoreCase(event.status())) {
            log.info("Ignoring non-filled trade event: {}", event.executionId());
            return;
        }

        String transactionType = event.side();

        if (!"BUY".equalsIgnoreCase(transactionType)
                && !"SELL".equalsIgnoreCase(transactionType)) {
            throw new IllegalArgumentException("Invalid trade side");
        }

        transactionType = transactionType.toUpperCase();

        if (event.totalAmount() == null
                || event.totalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid trade amount");
        }

        // Check duplicate Kafka event
        if (transactionRepository.existsByReferenceIdAndTransactionType(
                event.executionId(), transactionType)) {

            log.info("Duplicate trade event ignored: {}", event.executionId());
            return;
        }

        // Lock wallet row to prevent concurrent balance updates
        WalletEntity wallet = walletRepository
                .findByUserIdForUpdate(event.userId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Wallet not found for user: " + event.userId()
                ));

        BigDecimal currentBalance = wallet.getBalance();
        BigDecimal transactionAmount = event.totalAmount();

        BigDecimal updatedBalance;

        if ("BUY".equals(transactionType)) {

            // Prevent negative wallet balance
            if (currentBalance.compareTo(transactionAmount) < 0) {
                log.warn(
                        "Insufficient wallet balance for BUY. User: {}, Required: {}, Available: {}",
                        event.userId(), transactionAmount, currentBalance
                );
                return;
            }

            updatedBalance = currentBalance.subtract(transactionAmount);

        } else {

            updatedBalance = currentBalance.add(transactionAmount);
        }

        Instant now = Instant.now();

        // Update wallet balance
        wallet.updateBalance(updatedBalance, now);
        walletRepository.save(wallet);

        // Save BUY / SELL transaction history
        WalletTransactionEntity transaction =
                new WalletTransactionEntity(
                        UUID.randomUUID(),
                        wallet.getId(),
                        transactionType,
                        transactionAmount,
                        event.executionId(),
                        transactionType + " trade: " + event.symbol(),
                        now
                );

        transactionRepository.save(transaction);

        log.info(
                "{} transaction processed. User: {}, Amount: {}, New Balance: {}",
                transactionType,
                event.userId(),
                transactionAmount,
                updatedBalance
        );
    }

    @Transactional(readOnly = true)
    public List<WalletTransactionResponse> getTransactionHistory(UUID userId) {

        WalletEntity wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Wallet not found"
                ));

        return transactionRepository
                .findAllByWalletIdOrderByCreatedAtDesc(wallet.getId())
                .stream()
                .map(transaction -> new WalletTransactionResponse(
                        transaction.getId(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getReferenceId(),
                        transaction.getDescription(),
                        transaction.getCreatedAt()
                ))
                .toList();
    }

    private WalletResponse toResponse(WalletEntity wallet) {

        return new WalletResponse(
                wallet.getId(),
                wallet.getUserId(),
                wallet.getBalance()
        );
    }
}
