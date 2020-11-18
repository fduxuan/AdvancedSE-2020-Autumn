package com.example.scexaminemanuscripts.service;


import com.example.scexaminemanuscripts.domain.*;
import com.example.scexaminemanuscripts.exception.InvitationNotFoundException;
import com.example.scexaminemanuscripts.exception.MeetingNotFoundException;
import com.example.scexaminemanuscripts.exception.UserNotFoundException;
import com.example.scexaminemanuscripts.repository.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class RepositoryService {
    private UserRepository userRepository;
    private MeetingRepository meetingRepository;
    private AuthorityRepository authorityRepository;
    private InvitationRepository invitationRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    private ScoreRepository scoreRepository;
    private Sub_DiscussionRepository subDiscussionRepository;

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
     * 为指定user和meeting新增用户角色authority
     *
     * @param user      用户
     * @param authority 新增角色
     */
    void addUserAndMeetingAuthorities(User user, Meeting meeting, String authority) {
        Authority newAuthority = new Authority(authority);
        //修改user的authoritySet
        Set<Authority> userAuthority = new HashSet<>();
        if (user.getAuthorities() != null) {
            userAuthority = user.getAuthorities();
        }
        userAuthority.add(newAuthority);
        user.setAuthorities(userAuthority);
        //修改meeting的authoritySet
        Set<Authority> meetingAuthority = new HashSet<>();
        if (meeting.getAuthorities() != null) {
            meetingAuthority = meeting.getAuthorities();
        }
        meetingAuthority.add(newAuthority);
        meeting.setAuthorities(meetingAuthority);
        //存入数据库
        newAuthority.setUser(user);
        newAuthority.setMeeting(meeting);
        authorityRepository.save(newAuthority);
    }

    /**
     * 通过字符串形式的邀请ID找到对应的邀请，若找不到则抛出异常
     *
     * @param invitationId 字符串形式的邀请Id
     * @return 邀请Id对应的邀请
     */
    Invitation findByInvitationId(String invitationId) {
        Optional<Invitation> invitationOptional = invitationRepository.findById(Long.parseLong(invitationId));
        if (invitationOptional.isPresent()) {
            return invitationOptional.get();
        } else {
            throw new InvitationNotFoundException();
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
