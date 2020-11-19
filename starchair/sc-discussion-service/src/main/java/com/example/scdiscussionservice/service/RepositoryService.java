package com.example.scdiscussionservice.service;

import com.example.scdiscussionservice.domain.*;
import com.example.scdiscussionservice.exception.MeetingNotFoundException;
import com.example.scdiscussionservice.repository.MeetingRepository;
import com.example.scdiscussionservice.repository.Sub_DiscussionRepository;
import com.example.scdiscussionservice.repository.TopicPcMeetingArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class RepositoryService {

    private Sub_DiscussionRepository subDiscussionRepository;
    private MeetingRepository meetingRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    @Autowired
    public RepositoryService(Sub_DiscussionRepository subDiscussionRepository,
                             MeetingRepository meetingRepository,
                             TopicPcMeetingArticleRepository topicPcMeetingArticleRepository) {
        this.subDiscussionRepository = subDiscussionRepository;
        this.meetingRepository = meetingRepository;
        this.topicPcMeetingArticleRepository = topicPcMeetingArticleRepository;
    }

    /**
     * 为主题贴新增回复或回复的回复
     *
     * @param mainDiscussion 主题贴
     * @param subDiscussion  回复
     */
    void addNewComment(Main_Discussion mainDiscussion, Sub_Discussion subDiscussion) {
        List<Sub_Discussion> subDiscussions = new ArrayList<>();
        if (mainDiscussion.getSub_discussions() != null) {
            subDiscussions = mainDiscussion.getSub_discussions();
        }
        subDiscussions.add(subDiscussion);
        mainDiscussion.setSub_discussions(subDiscussions);
        subDiscussion.setMain_discussion(mainDiscussion);
        subDiscussionRepository.save(subDiscussion);
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
