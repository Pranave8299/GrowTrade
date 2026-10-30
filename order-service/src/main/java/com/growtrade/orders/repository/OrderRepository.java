package com.growtrade.orders.repository;

import com.growtrade.orders.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OrderRepository
        extends JpaRepository<OrderEntity, UUID> {
}