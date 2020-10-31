package com.example.scdiscussionservice.service;

import com.example.scdiscussionservice.domain.Authority;
import com.example.scdiscussionservice.domain.Meeting;
import com.example.scdiscussionservice.domain.User;
import com.example.scdiscussionservice.repository.AuthorityRepository;
import com.example.scdiscussionservice.repository.MeetingRepository;
import com.example.scdiscussionservice.repository.TopicPcMeetingArticleRepository;
import com.example.scdiscussionservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserCenterService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private MeetingRepository meetingRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    private RepositoryService repositoryService;

    @Autowired
    public UserCenterService(UserRepository userRepository,
                             AuthorityRepository authorityRepository,
                             MeetingRepository meetingRepository,
                             TopicPcMeetingArticleRepository topicPcMeetingArticleRepository,
                             RepositoryService repositoryService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
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

    /**
     * 查找我参与的所有会议
     *
     * @param username 用户名
     * @return 我参与的所有会议
     */
    public List<Object> myMeeting(String username) {
        User user = userRepository.findByUsername(username);
        Set<Authority> authoritySet = user.getAuthorities(); //通过角色列表查找对应的会议
        List<Meeting> meetingList = new ArrayList<>(); //返回的会议列表
        if (authoritySet != null) {
            for (Authority authority : authoritySet) {
                if (!meetingList.contains(authority.getMeeting()))
                    meetingList.add(authority.getMeeting());
            }
        }
        return repositoryService.getMeetingAndTopicList(meetingList);
    }

    /**
     * 返回指定用户在指定会议中的角色列表
     *
     * @param id       会议id
     * @param username 用户名
     * @return 指定用户在指定会议中的角色列表
     */
    public List<Authority> getUser_MeetingAuthorityList(String id, String username) {
        User user = userRepository.findByUsername(username);
        return authorityRepository.findByUserIdAndMeetingId(user.getId(), Long.parseLong(id));
    }

}
