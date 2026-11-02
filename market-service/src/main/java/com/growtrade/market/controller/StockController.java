package com.growtrade.market.controller;

import com.growtrade.market.dto.StockResponse;
import com.growtrade.market.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    public List<StockResponse> getAllStocks() {
        return stockService.getAllStocks();
    }

    @GetMapping("/{symbol}")
    public StockResponse getStockBySymbol(
            @PathVariable String symbol) {

        return stockService.getStockBySymbol(symbol);
    }
}