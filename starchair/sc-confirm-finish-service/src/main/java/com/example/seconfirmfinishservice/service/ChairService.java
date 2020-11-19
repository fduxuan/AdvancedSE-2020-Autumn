package com.example.seconfirmfinishservice.service;

import com.example.seconfirmfinishservice.domain.Meeting;
import com.example.seconfirmfinishservice.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChairService {
    private MeetingRepository meetingRepository;
    private RepositoryService repositoryService;
    private Random rand = new Random();

    @Autowired
    public ChairService(MeetingRepository meetingRepository,
                        RepositoryService repositoryService) {
        this.meetingRepository = meetingRepository;
        this.repositoryService = repositoryService;
    }


    /**
     * 用户确认结束投稿
     *
     * @param meetingId 会议id
     * @return 是否成功
     */
    public String finishContribution(String meetingId) {
        Meeting meeting = repositoryService.findByMeetingId(meetingId);
        meeting.setSubmitStatus("Reviewing");
        meetingRepository.save(meeting);
        return "success";
    }


}
