package com.example.scgetinvitionalsentservice.service;


import com.example.scgetinvitionalsentservice.domain.*;
import com.example.scgetinvitionalsentservice.exception.MeetingNotFoundException;
import com.example.scgetinvitionalsentservice.exception.UserNotFoundException;
import com.example.scgetinvitionalsentservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RepositoryService {
    private UserRepository userRepository;
    private MeetingRepository meetingRepository;
    private InvitationRepository invitationRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;

    @Autowired
    public RepositoryService(UserRepository userRepository,
                             MeetingRepository meetingRepository,
                             TopicPcMeetingArticleRepository topicPcMeetingArticleRepository,
                             InvitationRepository invitationRepository) {
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.invitationRepository = invitationRepository;
        this.topicPcMeetingArticleRepository = topicPcMeetingArticleRepository;

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




    /**
     * 通过字符串形式的userID找到对应的user，若找不到则抛出异常
     *
     * @param userId 字符串形式的userId
     * @return 邀请Id对应的user
     */
    User findByUserId(String userId) {
        Optional<User> userOptional = userRepository.findById(Long.parseLong(userId));
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UserNotFoundException();
        }
    }





    /**
     * 找到对应会议list的topic list并返回
     *
     * @param meetings meeting list
     * @return topic list
     */
    List<Object> getTopicList(List<Meeting> meetings) {
        List<Object> newList = new ArrayList<>();
        for (Meeting meeting : meetings) {
            List<Topic> topics = new ArrayList<>();
            List<TopicPcMeetingArticle> relation = topicPcMeetingArticleRepository.findByMeetingId(meeting.getId());
            for (TopicPcMeetingArticle rel : relation) {
                if (!topics.contains(rel.getTopic()))
                    topics.add(rel.getTopic());
            }
            newList.add(topics);
        }
        return newList;
    }



}
