package com.moyeo.backend.domain.participant.dto.response;

import java.util.List;

public record ParticipantResponse(Integer participantId, String name, List<Integer> candidateDateIds) { }
