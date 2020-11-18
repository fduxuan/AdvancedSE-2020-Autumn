package com.example.scapplymeetingservice.service;


import com.example.scapplymeetingservice.controller.request.MeetingRequest;
import com.example.scapplymeetingservice.domain.Authority;
import com.example.scapplymeetingservice.domain.Meeting;
import com.example.scapplymeetingservice.domain.Topic;
import com.example.scapplymeetingservice.domain.TopicPcMeetingArticle;
import com.example.scapplymeetingservice.repository.MeetingRepository;
import com.example.scapplymeetingservice.repository.TopicPcMeetingArticleRepository;
import com.example.scapplymeetingservice.repository.TopicRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashSet;

@Service
public class ApplyMeetingService {
    private MeetingRepository meetingRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    private TopicRepository topicRepository;

    /**
     * 会议申请实现
     *
     * @param request 会议申请基本信息
     * @return 新申请的会议
     */
    public Meeting applyConference(MeetingRequest request) {
        String shortenForm = request.getShortenForm();
        String fullName = request.getFullName();
        String time = request.getTime();
        String location = request.getLocation();
        String ddl = request.getDdl();
        String publishingTime = request.getPublishingTime();
        String applicant = request.getApplicant();
        String[] topics = request.getTopic();
        Meeting newMeeting = new Meeting(fullName, shortenForm, time, location, ddl,
                publishingTime, applicant, "false", "pass", new HashSet<Authority>());
        meetingRepository.save(newMeeting);
        for (String s : topics) {
            Topic topic = new Topic(s);
            TopicPcMeetingArticle relation = new TopicPcMeetingArticle();
            topicRepository.save(topic);
            relation.setTopic(topic);
            relation.setMeeting(newMeeting);
            topicPcMeetingArticleRepository.save(relation);
        }
        return newMeeting;
    }

}
