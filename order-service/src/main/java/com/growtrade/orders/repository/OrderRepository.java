package com.growtrade.orders.repository;

import com.growtrade.orders.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository
        extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findAllByUserIdOrderByCreatedAtDesc(UUID userId);
}