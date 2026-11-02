package com.growtrade.market.service;

import com.growtrade.market.dto.StockResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StockService {

    private final List<StockResponse> stocks = List.of(
            new StockResponse(
                    "RELIANCE",
                    "Reliance Industries",
                    "NSE",
                    "Energy",
                    new BigDecimal("2500.00")
            ),
            new StockResponse(
                    "TCS",
                    "Tata Consultancy Services",
                    "NSE",
                    "IT",
                    new BigDecimal("3800.00")
            ),
            new StockResponse(
                    "INFY",
                    "Infosys",
                    "NSE",
                    "IT",
                    new BigDecimal("1500.00")
            ),
            new StockResponse(
                    "HDFCBANK",
                    "HDFC Bank",
                    "NSE",
                    "Banking",
                    new BigDecimal("1700.00")
            ),
            new StockResponse(
                    "ICICIBANK",
                    "ICICI Bank",
                    "NSE",
                    "Banking",
                    new BigDecimal("1200.00")
            ),
            new StockResponse(
                    "TATAMOTORS",
                    "Tata Motors",
                    "NSE",
                    "Automobile",
                    new BigDecimal("700.00")
            )
    );

    public List<StockResponse> getAllStocks() {
        return stocks;
    }

    public StockResponse getStockBySymbol(String symbol) {

        return stocks.stream()
                .filter(stock -> stock.symbol()
                        .equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Stock not found: " + symbol
                ));
    }
}