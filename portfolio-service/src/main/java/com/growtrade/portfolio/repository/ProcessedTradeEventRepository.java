package com.growtrade.portfolio.repository;

import com.growtrade.portfolio.entity.ProcessedTradeEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedTradeEventRepository extends JpaRepository<ProcessedTradeEventEntity, UUID> {

}