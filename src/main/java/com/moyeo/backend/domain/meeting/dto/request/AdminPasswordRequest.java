package com.moyeo.backend.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminPasswordRequest(@NotBlank String adminPassword) {
}
