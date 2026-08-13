package com.moyeo.backend.domain.participant.service;

import com.moyeo.backend.domain.meeting.entity.CandidateDate;
import com.moyeo.backend.domain.meeting.entity.Meeting;
import com.moyeo.backend.domain.meeting.entity.MeetingStatus;
import com.moyeo.backend.domain.meeting.repository.CandidateDateRepository;
import com.moyeo.backend.domain.meeting.repository.MeetingRepository;
import com.moyeo.backend.domain.participant.dto.request.CreateParticipantRequest;
import com.moyeo.backend.domain.participant.dto.request.UpdateParticipantRequest;
import com.moyeo.backend.domain.participant.dto.response.CreateParticipantResponse;
import com.moyeo.backend.domain.participant.dto.response.ParticipantResponse;
import com.moyeo.backend.domain.participant.entity.Participant;
import com.moyeo.backend.domain.participant.entity.ParticipantAvailability;
import com.moyeo.backend.domain.participant.repository.ParticipantAvailabilityRepository;
import com.moyeo.backend.domain.participant.repository.ParticipantRepository;
import com.moyeo.backend.global.error.BadRequestException;
import com.moyeo.backend.global.error.ForbiddenException;
import com.moyeo.backend.global.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ParticipantService {
    private final MeetingRepository meetingRepository;
    private final CandidateDateRepository candidateDateRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantAvailabilityRepository availabilityRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public ParticipantService(MeetingRepository meetingRepository,
                              CandidateDateRepository candidateDateRepository,
                              ParticipantRepository participantRepository,
                              ParticipantAvailabilityRepository availabilityRepository) {
        this.meetingRepository = meetingRepository;
        this.candidateDateRepository = candidateDateRepository;
        this.participantRepository = participantRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Transactional
    public CreateParticipantResponse create(String meetingCode, CreateParticipantRequest request) {
        Meeting meeting = findMeeting(meetingCode);
        ensureOpen(meeting);
        List<Integer> dateIds = validateCandidateDates(meeting, request.candidateDateIds());

        String token = createToken();
        Participant participant = participantRepository.save(
                new Participant(meeting.getId(), request.name().trim(), hashToken(token)));
        saveAvailabilities(participant.getId(), dateIds);
        return new CreateParticipantResponse(participant.getId(), token);
    }

    public ParticipantResponse getMine(String meetingCode, String token) {
        Meeting meeting = findMeeting(meetingCode);
        Participant participant = findParticipant(meeting, token);
        return toResponse(participant);
    }

    @Transactional
    public ParticipantResponse updateMine(String meetingCode, String token, UpdateParticipantRequest request) {
        Meeting meeting = findMeeting(meetingCode);
        ensureOpen(meeting);
        Participant participant = findParticipant(meeting, token);
        List<Integer> dateIds = validateCandidateDates(meeting, request.candidateDateIds());

        participant.updateName(request.name().trim());
        availabilityRepository.deleteAllByParticipantId(participant.getId());
        saveAvailabilities(participant.getId(), dateIds);
        return new ParticipantResponse(participant.getId(), participant.getName(), dateIds);
    }

    private Meeting findMeeting(String meetingCode) {
        return meetingRepository.findByMeetingCode(meetingCode)
                .orElseThrow(() -> new NotFoundException("모임을 찾을 수 없습니다."));
    }

    private Participant findParticipant(Meeting meeting, String token) {
        if (token == null || token.isBlank()) throw new ForbiddenException("참여자 토큰이 필요합니다.");
        return participantRepository.findByMeetingIdAndEditTokenHash(meeting.getId(), hashToken(token))
                .orElseThrow(() -> new ForbiddenException("참여자 토큰이 올바르지 않습니다."));
    }

    private void ensureOpen(Meeting meeting) {
        if (meeting.getStatus() != MeetingStatus.OPEN) {
            throw new BadRequestException("확정된 모임에는 참여하거나 정보를 수정할 수 없습니다.");
        }
    }

    private List<Integer> validateCandidateDates(Meeting meeting, List<Integer> requestedIds) {
        List<Integer> distinctIds = requestedIds.stream().distinct().toList();
        if (distinctIds.size() != requestedIds.size()) throw new BadRequestException("후보 날짜는 중복될 수 없습니다.");

        Set<Integer> meetingDateIds = new HashSet<>(candidateDateRepository
                .findAllByMeetingIdOrderByCandidateDateAsc(meeting.getId()).stream().map(CandidateDate::getId).toList());
        if (!meetingDateIds.containsAll(distinctIds)) {
            throw new BadRequestException("해당 모임의 후보 날짜만 선택할 수 있습니다.");
        }
        return distinctIds;
    }

    private void saveAvailabilities(Integer participantId, List<Integer> dateIds) {
        availabilityRepository.saveAll(dateIds.stream()
                .map(dateId -> new ParticipantAvailability(participantId, dateId)).toList());
    }

    private ParticipantResponse toResponse(Participant participant) {
        List<Integer> dateIds = availabilityRepository.findAllByParticipantId(participant.getId()).stream()
                .map(ParticipantAvailability::getCandidateDateId).sorted().toList();
        return new ParticipantResponse(participant.getId(), participant.getName(), dateIds);
    }

    private String createToken() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256을 사용할 수 없습니다.", exception);
        }
    }
}
