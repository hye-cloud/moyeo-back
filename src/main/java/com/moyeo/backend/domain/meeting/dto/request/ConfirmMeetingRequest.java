package com.moyeo.backend.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ConfirmMeetingRequest(@NotNull Integer candidateDateId, @NotBlank String adminPassword) {
}
