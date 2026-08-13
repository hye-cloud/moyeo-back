package com.moyeo.backend.domain.participant.repository;

import com.moyeo.backend.domain.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    Optional<Participant> findByMeetingIdAndEditTokenHash(Integer meetingId, String editTokenHash);
    long countByMeetingId(Integer meetingId);
}
