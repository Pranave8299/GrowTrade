package com.growtrade.market.controller;

import com.growtrade.market.dto.PriceUpdate;
import com.growtrade.market.service.StockPriceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
public class StockPriceController {

    private final StockPriceService stockPriceService;

    public StockPriceController(
            StockPriceService stockPriceService) {

        this.stockPriceService = stockPriceService;
    }

    @GetMapping("/{symbol}/price")
    public PriceUpdate getStockPrice(
            @PathVariable String symbol) {

        return stockPriceService.getStockPrice(symbol);
    }
}