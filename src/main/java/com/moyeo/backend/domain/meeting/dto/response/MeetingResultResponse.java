package com.moyeo.backend.domain.meeting.dto.response;

import com.moyeo.backend.domain.meeting.entity.MeetingStatus;

import java.time.LocalDate;
import java.util.List;

public record MeetingResultResponse(
        String meetingCode,
        String meetingName,
        long participantCount,
        MeetingStatus status,
        LocalDate confirmedDate,
        List<DateResult> dateResults,
        List<DateResult> recommendations
) {
    public record DateResult(
            Integer candidateDateId,
            LocalDate date,
            int availableCount,
            List<ParticipantSummary> participants
    ) { }

    public record ParticipantSummary(Integer participantId, String name) { }
}
