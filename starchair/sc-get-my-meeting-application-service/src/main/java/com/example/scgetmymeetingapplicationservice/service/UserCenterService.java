package com.example.scgetmymeetingapplicationservice.service;

import com.example.scgetmymeetingapplicationservice.domain.Meeting;
import com.example.scgetmymeetingapplicationservice.repository.MeetingRepository;
import com.example.scgetmymeetingapplicationservice.repository.TopicPcMeetingArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCenterService {
    private MeetingRepository meetingRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    private RepositoryService repositoryService;

    @Autowired
    public UserCenterService(MeetingRepository meetingRepository,
                             TopicPcMeetingArticleRepository topicPcMeetingArticleRepository,
                             RepositoryService repositoryService) {
        this.meetingRepository = meetingRepository;
        this.topicPcMeetingArticleRepository = topicPcMeetingArticleRepository;
        this.repositoryService = repositoryService;
    }

    /**
     * 查找所有我申请的会议并返回
     *
     * @return 所有我申请的会议list
     */
    public List<Object> myMeetingApplication(String username) {
        List<Meeting> uncheckedMeeting = meetingRepository.findByApplicant(username);
        return repositoryService.getMeetingAndTopicList(uncheckedMeeting);
    }


}
