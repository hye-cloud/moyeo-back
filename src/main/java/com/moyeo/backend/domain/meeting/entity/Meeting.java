package com.moyeo.backend.domain.meeting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meeting")
public class Meeting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 12)
    private String meetingCode;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 255)
    private String adminPasswordHash;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Column
    private Integer confirmedCandidateDateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MeetingStatus status;

    public Meeting(String meetingCode, String title, String description, String adminPasswordHash) {
        this.meetingCode = meetingCode;
        this.title = title;
        this.description = description;
        this.adminPasswordHash = adminPasswordHash;
        this.status = MeetingStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String title, String description) {
        this.title = title;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void confirm(Integer candidateDateId) {
        this.confirmedCandidateDateId = candidateDateId;
        this.status = MeetingStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

}
