package com.growtrade.portfolio.controller;

import com.growtrade.portfolio.dto.HoldingResponse;
import com.growtrade.portfolio.service.HoldingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/portfolio")
public class HoldingController {

    private final HoldingService holdingService;

    public HoldingController(HoldingService holdingService) {
        this.holdingService = holdingService;
    }

    @GetMapping("/{userId}/holdings")
    public List<HoldingResponse> getHoldings(
            @PathVariable UUID userId) {

        return holdingService.getHoldings(userId);
    }
}