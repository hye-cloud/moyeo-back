package com.moyeo.backend.domain.meeting.service;

import com.moyeo.backend.domain.meeting.dto.request.CreateMeetingRequest;
import com.moyeo.backend.domain.meeting.dto.response.CreateMeetingResponse;
import com.moyeo.backend.domain.meeting.dto.response.MeetingResponse;
import com.moyeo.backend.domain.meeting.dto.request.UpdateMeetingRequest;
import com.moyeo.backend.domain.meeting.entity.CandidateDate;
import com.moyeo.backend.domain.meeting.entity.Meeting;
import com.moyeo.backend.domain.meeting.repository.CandidateDateRepository;
import com.moyeo.backend.domain.meeting.repository.MeetingRepository;
import com.moyeo.backend.domain.participant.repository.ParticipantRepository;
import com.moyeo.backend.global.error.BadRequestException;
import com.moyeo.backend.global.error.ForbiddenException;
import com.moyeo.backend.global.error.NotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeetingService {
    private static final char[] CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
    private final MeetingRepository meetingRepository;
    private final CandidateDateRepository candidateDateRepository;
    private final PasswordEncoder passwordEncoder;
    private final ParticipantRepository participantRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    public MeetingService(MeetingRepository meetingRepository, CandidateDateRepository candidateDateRepository,
                          PasswordEncoder passwordEncoder, ParticipantRepository participantRepository) {
        this.meetingRepository = meetingRepository;
        this.candidateDateRepository = candidateDateRepository;
        this.passwordEncoder = passwordEncoder;
        this.participantRepository = participantRepository;
    }

    @Transactional
    public CreateMeetingResponse create(CreateMeetingRequest request) {
        List<java.time.LocalDate> dates = request.candidateDates().stream().distinct().sorted().toList();
        if (dates.size() != request.candidateDates().size()) throw new BadRequestException("후보 날짜는 중복될 수 없습니다.");

        Meeting meeting = meetingRepository.save(new Meeting(createUniqueCode(), request.title().trim(),
                request.description(), passwordEncoder.encode(request.adminPassword())));
        candidateDateRepository.saveAll(dates.stream().map(date -> new CandidateDate(meeting.getId(), date)).toList());
        return new CreateMeetingResponse(meeting.getMeetingCode());
    }

    public MeetingResponse get(String code) {
        Meeting meeting = find(code);
        return MeetingResponse.of(meeting,
                candidateDateRepository.findAllByMeetingIdOrderByCandidateDateAsc(meeting.getId()),
                participantRepository.countByMeetingId(meeting.getId()));
    }

    @Transactional
    public MeetingResponse update(String code, UpdateMeetingRequest request) {
        Meeting meeting = find(code);
        verifyPassword(meeting, request.adminPassword());
        meeting.update(request.title().trim(), request.description());
        return get(code);
    }

    @Transactional
    public void confirm(String code, Integer candidateDateId, String password) {
        Meeting meeting = find(code);
        verifyPassword(meeting, password);
        if (!candidateDateRepository.existsByIdAndMeetingId(candidateDateId, meeting.getId())) {
            throw new BadRequestException("해당 모임의 후보 날짜가 아닙니다.");
        }
        meeting.confirm(candidateDateId);
    }

    @Transactional
    public void delete(String code, String password) {
        Meeting meeting = find(code);
        verifyPassword(meeting, password);
        candidateDateRepository.deleteAllByMeetingId(meeting.getId());
        meetingRepository.delete(meeting);
    }

    private Meeting find(String code) {
        return meetingRepository.findByMeetingCode(code).orElseThrow(() -> new NotFoundException("모임을 찾을 수 없습니다."));
    }

    private void verifyPassword(Meeting meeting, String password) {
        if (!passwordEncoder.matches(password, meeting.getAdminPasswordHash())) {
            throw new ForbiddenException("관리자 비밀번호가 올바르지 않습니다.");
        }
    }

    private String createUniqueCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder code = new StringBuilder(8);
            for (int i = 0; i < 8; i++) code.append(CODE_CHARS[secureRandom.nextInt(CODE_CHARS.length)]);
            if (!meetingRepository.existsByMeetingCode(code.toString())) return code.toString();
        }
        throw new IllegalStateException("모임 코드를 생성하지 못했습니다.");
    }
}
