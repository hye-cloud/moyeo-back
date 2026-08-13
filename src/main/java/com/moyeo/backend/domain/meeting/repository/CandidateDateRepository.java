package com.moyeo.backend.domain.meeting.repository;

import com.moyeo.backend.domain.meeting.entity.CandidateDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateDateRepository extends JpaRepository<CandidateDate, Integer> {
    List<CandidateDate> findAllByMeetingIdOrderByCandidateDateAsc(Integer meetingId);
    boolean existsByIdAndMeetingId(Integer id, Integer meetingId);
    void deleteAllByMeetingId(Integer meetingId);
}
