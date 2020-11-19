package com.example.scstartsubmitservice.service;


import com.example.scstartsubmitservice.domain.Meeting;
import com.example.scstartsubmitservice.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ChairService {
    private MeetingRepository meetingRepository;
    private RepositoryService repositoryService;
    private Random rand = new Random();

    /**
     * 修改会议的投稿状态
     *
     * @param id           会议id
     * @param submitStatus 变量记录是否开启投稿
     * @return 是否修改成功message
     */
    public String changeSubmitStatus(String id, String submitStatus) {
        Meeting meeting = repositoryService.findByMeetingId(id);
        meeting.setSubmitStatus(submitStatus);
        meetingRepository.save(meeting);
        return "success";
    }
}
