package com.example.scgetmymeetingapplicationservice.service;

import com.example.scgetmymeetingapplicationservice.domain.Meeting;
import com.example.scgetmymeetingapplicationservice.domain.Topic;
import com.example.scgetmymeetingapplicationservice.domain.TopicPcMeetingArticle;
import com.example.scgetmymeetingapplicationservice.repository.MeetingRepository;
import com.example.scgetmymeetingapplicationservice.repository.TopicPcMeetingArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RepositoryService {
    private MeetingRepository meetingRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    @Autowired
    public RepositoryService(TopicPcMeetingArticleRepository topicPcMeetingArticleRepository,
                             MeetingRepository meetingRepository){
        this.topicPcMeetingArticleRepository = topicPcMeetingArticleRepository;
        this.meetingRepository = meetingRepository;
    }


    /**
     * 遍历传入的meeting list，查找对应的topic list，将两个list按顺序合并为一个list返回
     *
     * @param meetings 传入的meeting list
     * @return 新的带topic的list
     */
    List<Object> getMeetingAndTopicList(List<Meeting> meetings) {
        List<Object> newList = new ArrayList<>();
        for (Meeting meeting : meetings) {
            List<Topic> topics = new ArrayList<>();
            newList.add(meeting);
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
