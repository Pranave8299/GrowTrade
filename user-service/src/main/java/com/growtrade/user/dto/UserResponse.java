
package com.growtrade.user.dto;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String fullName,
        String email,
        String role
) {
}
