package com.example.scgetinvitionalsentservice.service;

import com.example.scgetinvitionalsentservice.domain.*;
import com.example.scgetinvitionalsentservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserCenterService {
    private UserRepository userRepository;
    private MeetingRepository meetingRepository;
    private InvitationRepository invitationRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    private RepositoryService repositoryService;

    @Autowired
    public UserCenterService(UserRepository userRepository,
                             MeetingRepository meetingRepository,
                             InvitationRepository invitationRepository,
                             TopicPcMeetingArticleRepository topicPcMeetingArticleRepository,
                             RepositoryService repositoryService) {
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.invitationRepository = invitationRepository;
        this.topicPcMeetingArticleRepository = topicPcMeetingArticleRepository;
        this.repositoryService = repositoryService;
    }



    /**
     * 通过用户名查找用户发送的所有邀请
     * 遍历邀请列表，通过被邀请人ID查找被邀请人信息，通过会议ID查找会议信息
     *
     * @param username 用户名
     * @return Map 所有被邀请人信息,所有会议信息和邀请状态
     */
    public Map<String, Object> invitationISent(String username) {
        List<Invitation> invitationList = invitationRepository.findByInviter(username); //返回的所有邀请信息
        List<Meeting> meetingList = new ArrayList<>(); //返回的所有会议信息
        List<User> userList = new ArrayList<>(); //返回的所有被邀请人
        for (Invitation invitations : invitationList) {
            meetingList.add(repositoryService.findByMeetingId(invitations.getMeetingId()));
            userList.add(repositoryService.findByUserId(invitations.getInvitee()));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("meetingDetails", meetingList);
        map.put("inviteeDetails", userList);
        map.put("invitationDetails", invitationList);
        map.put("topicDetails", repositoryService.getTopicList(meetingList));
        return map;
    }
}
