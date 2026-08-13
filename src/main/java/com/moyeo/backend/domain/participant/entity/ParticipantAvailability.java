package com.moyeo.backend.domain.participant.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "participant_availability")
public class ParticipantAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer participantId;

    @Column(nullable = false)
    private Integer candidateDateId;

    @Column(nullable = false)
    private LocalDateTime createdAt;


}
