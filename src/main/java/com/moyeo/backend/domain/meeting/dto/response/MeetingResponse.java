package com.moyeo.backend.domain.meeting.dto.response;

import com.moyeo.backend.domain.meeting.entity.CandidateDate;
import com.moyeo.backend.domain.meeting.entity.Meeting;
import com.moyeo.backend.domain.meeting.entity.MeetingStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MeetingResponse(
        String meetingCode,
        String title,
        String description,
        MeetingStatus status,
        List<CandidateDateResponse> candidateDates,
        long participantCount,
        Integer confirmedCandidateDateId,
        LocalDate confirmedDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MeetingResponse of(Meeting meeting, List<CandidateDate> dates, long participantCount) {
        LocalDate confirmedDate = dates.stream()
                .filter(date -> date.getId().equals(meeting.getConfirmedCandidateDateId()))
                .map(CandidateDate::getCandidateDate)
                .findFirst()
                .orElse(null);
        return new MeetingResponse(meeting.getMeetingCode(), meeting.getTitle(), meeting.getDescription(),
                meeting.getStatus(), dates.stream().map(CandidateDateResponse::from).toList(),
                participantCount, meeting.getConfirmedCandidateDateId(), confirmedDate,
                meeting.getCreatedAt(), meeting.getUpdatedAt());
    }

    public record CandidateDateResponse(Integer id, LocalDate date) {
        public static CandidateDateResponse from(CandidateDate candidateDate) {
            return new CandidateDateResponse(candidateDate.getId(), candidateDate.getCandidateDate());
        }
    }
}
