package com.moyeo.backend.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateMeetingRequest(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 500) String description,
        @NotBlank String adminPassword
) {
}
