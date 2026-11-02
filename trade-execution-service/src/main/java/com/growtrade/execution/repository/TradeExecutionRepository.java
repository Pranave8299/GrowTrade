package com.growtrade.execution.repository;
import com.growtrade.execution.entity.TradeExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TradeExecutionRepository
        extends JpaRepository<TradeExecutionEntity, UUID> {

    Optional<TradeExecutionEntity> findByOrderId(UUID orderId);

    boolean existsByOrderId(UUID orderId);
}