package com.moyeo.backend.domain.participant.controller;

import com.moyeo.backend.domain.participant.dto.request.CreateParticipantRequest;
import com.moyeo.backend.domain.participant.dto.request.UpdateParticipantRequest;
import com.moyeo.backend.domain.participant.dto.response.CreateParticipantResponse;
import com.moyeo.backend.domain.participant.dto.response.ParticipantResponse;
import com.moyeo.backend.domain.participant.service.ParticipantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/meetings/{meetingCode}/participants")
public class ParticipantController {
    public static final String PARTICIPANT_TOKEN_HEADER = "X-Participant-Token";
    private final ParticipantService participantService;

    public ParticipantController(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @PostMapping
    public ResponseEntity<CreateParticipantResponse> createParticipant(
            @PathVariable String meetingCode,
            @Valid @RequestBody CreateParticipantRequest request) {
        CreateParticipantResponse response = participantService.create(meetingCode, request);
        return ResponseEntity.created(URI.create("/api/meetings/" + meetingCode + "/participants/me"))
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ParticipantResponse> getMyParticipant(
            @PathVariable String meetingCode,
            @RequestHeader(PARTICIPANT_TOKEN_HEADER) String participantToken) {
        return ResponseEntity.ok(participantService.getMine(meetingCode, participantToken));
    }

    @PatchMapping("/me")
    public ResponseEntity<ParticipantResponse> updateMyParticipant(
            @PathVariable String meetingCode,
            @RequestHeader(PARTICIPANT_TOKEN_HEADER) String participantToken,
            @Valid @RequestBody UpdateParticipantRequest request) {
        return ResponseEntity.ok(participantService.updateMine(meetingCode, participantToken, request));
    }
}
