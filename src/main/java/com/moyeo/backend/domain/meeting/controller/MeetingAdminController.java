package com.moyeo.backend.domain.meeting.controller;

import com.moyeo.backend.domain.meeting.dto.request.AdminPasswordRequest;
import com.moyeo.backend.domain.meeting.dto.request.ConfirmMeetingRequest;
import com.moyeo.backend.domain.meeting.dto.response.MeetingResponse;
import com.moyeo.backend.domain.meeting.dto.request.UpdateMeetingRequest;
import com.moyeo.backend.domain.meeting.service.MeetingService;
import com.moyeo.backend.domain.participant.service.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings/{meetingCode}/admin")
public class MeetingAdminController {
    private final MeetingService meetingService;
    private final ParticipantService participantService;

    public MeetingAdminController(MeetingService meetingService, ParticipantService participantService) {
        this.meetingService = meetingService;
        this.participantService = participantService;
    }

    @PatchMapping
    public ResponseEntity<MeetingResponse> updateMeeting(@PathVariable String meetingCode,
                                                          @Valid @RequestBody UpdateMeetingRequest request) {
        return ResponseEntity.ok(meetingService.update(meetingCode, request));
    }

    @PatchMapping("/confirmation")
    public ResponseEntity<Void> confirmDate(@PathVariable String meetingCode,
                                            @Valid @RequestBody ConfirmMeetingRequest request) {
        meetingService.confirm(meetingCode, request.candidateDateId(), request.adminPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMeeting(@PathVariable String meetingCode,
                                              @Valid @RequestBody AdminPasswordRequest request) {
        meetingService.delete(meetingCode, request.adminPassword());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/participants/{participantId}")
    public ResponseEntity<Void> deleteParticipant(@PathVariable String meetingCode,
                                                  @PathVariable Integer participantId,
                                                  @Valid @RequestBody AdminPasswordRequest request) {
        participantService.deleteByAdmin(meetingCode, participantId, request.adminPassword());
        return ResponseEntity.noContent().build();
    }
}
