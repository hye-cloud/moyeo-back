package com.moyeo.backend.domain.meeting.repository;

import com.moyeo.backend.domain.meeting.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Integer> {
    Optional<Meeting> findByMeetingCode(String meetingCode);
    boolean existsByMeetingCode(String meetingCode);
}
