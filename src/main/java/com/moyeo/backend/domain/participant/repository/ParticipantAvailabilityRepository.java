package com.moyeo.backend.domain.participant.repository;

import com.moyeo.backend.domain.participant.entity.ParticipantAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantAvailabilityRepository extends JpaRepository<ParticipantAvailability, Integer> {
    List<ParticipantAvailability> findAllByParticipantId(Integer participantId);
    List<ParticipantAvailability> findAllByParticipantIdIn(List<Integer> participantIds);
    void deleteAllByParticipantId(Integer participantId);
}
