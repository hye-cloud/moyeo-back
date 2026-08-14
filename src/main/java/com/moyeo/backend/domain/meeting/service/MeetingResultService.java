package com.moyeo.backend.domain.meeting.service;

import com.moyeo.backend.domain.meeting.dto.response.MeetingResultResponse;
import com.moyeo.backend.domain.meeting.dto.response.MeetingResultResponse.DateResult;
import com.moyeo.backend.domain.meeting.dto.response.MeetingResultResponse.ParticipantSummary;
import com.moyeo.backend.domain.meeting.entity.CandidateDate;
import com.moyeo.backend.domain.meeting.entity.Meeting;
import com.moyeo.backend.domain.meeting.repository.CandidateDateRepository;
import com.moyeo.backend.domain.meeting.repository.MeetingRepository;
import com.moyeo.backend.domain.participant.entity.Participant;
import com.moyeo.backend.domain.participant.entity.ParticipantAvailability;
import com.moyeo.backend.domain.participant.repository.ParticipantAvailabilityRepository;
import com.moyeo.backend.domain.participant.repository.ParticipantRepository;
import com.moyeo.backend.global.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class MeetingResultService {
    private final MeetingRepository meetingRepository;
    private final CandidateDateRepository candidateDateRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantAvailabilityRepository availabilityRepository;

    public MeetingResultService(MeetingRepository meetingRepository,
                                CandidateDateRepository candidateDateRepository,
                                ParticipantRepository participantRepository,
                                ParticipantAvailabilityRepository availabilityRepository) {
        this.meetingRepository = meetingRepository;
        this.candidateDateRepository = candidateDateRepository;
        this.participantRepository = participantRepository;
        this.availabilityRepository = availabilityRepository;
    }

    public MeetingResultResponse getResult(String meetingCode) {
        Meeting meeting = meetingRepository.findByMeetingCode(meetingCode)
                .orElseThrow(() -> new NotFoundException("모임을 찾을 수 없습니다."));
        List<CandidateDate> candidateDates = candidateDateRepository
                .findAllByMeetingIdOrderByCandidateDateAsc(meeting.getId());
        List<Participant> participants = participantRepository
                .findAllByMeetingIdOrderByCreatedAtAsc(meeting.getId());

        Map<Integer, Participant> participantById = new HashMap<>();
        participants.forEach(participant -> participantById.put(participant.getId(), participant));
        Map<Integer, List<ParticipantSummary>> participantsByDate = new HashMap<>();
        if (!participants.isEmpty()) {
            List<Integer> participantIds = participants.stream().map(Participant::getId).toList();
            for (ParticipantAvailability availability : availabilityRepository.findAllByParticipantIdIn(participantIds)) {
                Participant participant = participantById.get(availability.getParticipantId());
                if (participant != null) {
                    participantsByDate.computeIfAbsent(availability.getCandidateDateId(), ignored -> new ArrayList<>())
                            .add(new ParticipantSummary(participant.getId(), participant.getName()));
                }
            }
        }

        List<DateResult> dateResults = candidateDates.stream().map(candidateDate -> {
            List<ParticipantSummary> availableParticipants = participantsByDate
                    .getOrDefault(candidateDate.getId(), List.of()).stream()
                    .sorted(Comparator.comparing(ParticipantSummary::name)
                            .thenComparing(ParticipantSummary::participantId))
                    .toList();
            return new DateResult(candidateDate.getId(), candidateDate.getCandidateDate(),
                    availableParticipants.size(), availableParticipants);
        }).toList();

        List<DateResult> recommendations = dateResults.stream()
                .sorted(Comparator.comparingInt(DateResult::availableCount).reversed()
                        .thenComparing(DateResult::date))
                .limit(3)
                .toList();
        LocalDate confirmedDate = candidateDates.stream()
                .filter(date -> date.getId().equals(meeting.getConfirmedCandidateDateId()))
                .map(CandidateDate::getCandidateDate)
                .findFirst()
                .orElse(null);

        return new MeetingResultResponse(meeting.getMeetingCode(), meeting.getTitle(), participants.size(),
                meeting.getStatus(), confirmedDate, dateResults, recommendations);
    }
}
