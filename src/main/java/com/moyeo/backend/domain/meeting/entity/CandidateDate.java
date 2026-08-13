package com.moyeo.backend.domain.meeting.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "candidate_date")
public class CandidateDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer meetingId;

    @Column(nullable = false)
    private LocalDate candidateDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public CandidateDate(Integer meetingId, LocalDate candidateDate) {
        this.meetingId = meetingId;
        this.candidateDate = candidateDate;
        this.createdAt = LocalDateTime.now();
    }

}
