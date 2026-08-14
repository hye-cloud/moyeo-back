package com.moyeo.backend.domain.participant.repository;

import com.moyeo.backend.domain.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Integer> {
    Optional<Participant> findByMeetingIdAndEditTokenHash(Integer meetingId, String editTokenHash);
    Optional<Participant> findByIdAndMeetingId(Integer id, Integer meetingId);
    List<Participant> findAllByMeetingIdOrderByCreatedAtAsc(Integer meetingId);
    long countByMeetingId(Integer meetingId);
}
