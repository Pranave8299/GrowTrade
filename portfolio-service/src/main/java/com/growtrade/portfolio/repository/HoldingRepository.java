package com.growtrade.portfolio.repository;

import com.growtrade.portfolio.entity.HoldingEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HoldingRepository
        extends JpaRepository<HoldingEntity, UUID> {

    List<HoldingEntity> findAllByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<HoldingEntity> findByUserIdAndSymbol(
            UUID userId,
            String symbol
    );
}