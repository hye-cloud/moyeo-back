package com.moyeo.backend.domain.meeting.controller;

import com.moyeo.backend.domain.meeting.dto.request.CreateMeetingRequest;
import com.moyeo.backend.domain.meeting.dto.response.CreateMeetingResponse;
import com.moyeo.backend.domain.meeting.dto.response.MeetingResponse;
import com.moyeo.backend.domain.meeting.dto.response.MeetingResultResponse;
import com.moyeo.backend.domain.meeting.service.MeetingService;
import com.moyeo.backend.domain.meeting.service.MeetingResultService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/meetings")
public class MeetingController {
    private final MeetingService meetingService;
    private final MeetingResultService meetingResultService;

    public MeetingController(MeetingService meetingService, MeetingResultService meetingResultService) {
        this.meetingService = meetingService;
        this.meetingResultService = meetingResultService;
    }

    @PostMapping
    public ResponseEntity<CreateMeetingResponse> createMeeting(@Valid @RequestBody CreateMeetingRequest request) {
        CreateMeetingResponse response = meetingService.create(request);
        return ResponseEntity.created(URI.create("/api/meetings/" + response.meetingCode())).body(response);
    }

    @GetMapping("/{meetingCode}")
    public ResponseEntity<MeetingResponse> viewMeeting(@PathVariable String meetingCode) {
        return ResponseEntity.ok(meetingService.get(meetingCode));
    }

    @GetMapping("/{meetingCode}/results")
    public ResponseEntity<MeetingResultResponse> viewResults(@PathVariable String meetingCode) {
        return ResponseEntity.ok(meetingResultService.getResult(meetingCode));
    }
}
