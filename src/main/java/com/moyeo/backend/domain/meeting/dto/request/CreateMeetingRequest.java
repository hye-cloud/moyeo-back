package com.moyeo.backend.domain.meeting.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateMeetingRequest(
        @NotBlank @Size(max = 100) String title,
        @Size(max = 500) String description,
        @NotBlank @Size(min = 4, max = 72) String adminPassword,
        @NotEmpty List<LocalDate> candidateDates
) {
}
