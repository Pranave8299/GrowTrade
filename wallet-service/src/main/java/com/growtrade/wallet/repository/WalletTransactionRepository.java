package com.growtrade.wallet.repository;

import com.growtrade.wallet.entity.WalletTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransactionEntity, UUID> {

    List<WalletTransactionEntity>
    findAllByWalletIdOrderByCreatedAtDesc(UUID walletId);
    boolean existsByReferenceIdAndTransactionType(UUID referenceId, String transactionType);
}