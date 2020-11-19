package com.example.scstartsubmitservice.service;


import com.example.scstartsubmitservice.domain.*;
import com.example.scstartsubmitservice.exception.MeetingNotFoundException;
import com.example.scstartsubmitservice.repository.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RepositoryService {
    private MeetingRepository meetingRepository;

    /**
     * 通过字符串形式的会议ID找到对应的会议，若找不到则抛出异常
     *
     * @param meetingId 字符串形式的会议Id
     * @return 会议Id对应的会议
     */
    Meeting findByMeetingId(String meetingId) {
        Optional<Meeting> meetingOptional = meetingRepository.findById(Long.parseLong(meetingId));
        if (meetingOptional.isPresent()) {
            return meetingOptional.get();
        } else {
            throw new MeetingNotFoundException();
        }
    }
}
