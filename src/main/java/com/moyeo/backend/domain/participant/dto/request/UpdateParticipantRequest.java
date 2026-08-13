package com.moyeo.backend.domain.participant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateParticipantRequest(
        @NotBlank @Size(max = 30) String name,
        @NotEmpty List<@NotNull Integer> candidateDateIds
) { }
