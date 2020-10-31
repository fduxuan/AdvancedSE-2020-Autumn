package com.example.seconfirmfinishservice.service;


import com.example.seconfirmfinishservice.domain.*;
import com.example.seconfirmfinishservice.exception.MeetingNotFoundException;
import com.example.seconfirmfinishservice.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RepositoryService {
    private MeetingRepository meetingRepository;
    @Autowired
    public RepositoryService(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }

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
