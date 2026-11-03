
package com.growtrade.user.dto;

import java.util.UUID;

public record LoginResponse(
        String accessToken,
        String tokenType,
        UUID userId,
        String email,
        String role
) {
}
