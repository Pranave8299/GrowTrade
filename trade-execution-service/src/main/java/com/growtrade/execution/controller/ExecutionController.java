package com.growtrade.execution.controller;

import com.growtrade.execution.model.TradeExecutionResult;
import com.growtrade.execution.service.TradeExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/executions")
public class ExecutionController {

    private final TradeExecutionService tradeExecutionService;

    public ExecutionController(TradeExecutionService tradeExecutionService) {
        this.tradeExecutionService = tradeExecutionService;
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<TradeExecutionResult> getExecutionByOrderId(
            @PathVariable UUID orderId) {

        TradeExecutionResult result =
                tradeExecutionService.getExecutionByOrderId(orderId);

        return ResponseEntity.ok(result);
    }
}