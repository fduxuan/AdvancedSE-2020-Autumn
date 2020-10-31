package com.example.scgetinvitationservice.service;

import com.example.scgetinvitationservice.domain.*;
import com.example.scgetinvitationservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserCenterService {
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private MeetingRepository meetingRepository;
    private InvitationRepository invitationRepository;
    private RepositoryService repositoryService;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;

    /**
     * 通过用户名查找用户收到的所有邀请
     * 遍历邀请列表，通过邀请人用户名查找邀请人信息，通过会议ID查找会议信息
     *
     * @param id 用户的id
     * @return Map 所有被邀请人信息,所有会议信息和邀请状态
     */
    public Map<String, Object> invitationIReceived(String id) {
        List<Invitation> invitationList = invitationRepository.findByInviteeAndInvitationStatus(id, "false");//返回的所有邀请信息
        List<Meeting> meetingList = new ArrayList<>(); //返回的所有会议信息
        List<User> userList = new ArrayList<>(); //返回的所有邀请人信息
        for (Invitation invitations : invitationList) {
            meetingList.add(repositoryService.findByMeetingId(invitations.getMeetingId()));
            userList.add(userRepository.findByUsername(invitations.getInviter()));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("meetingDetails", meetingList);
        map.put("inviterDetails", userList);
        map.put("invitationDetails", invitationList);
        map.put("topicDetails", repositoryService.getTopicList(meetingList));
        return map;
    }

    /**
     * 通过邀请ID查找邀请，修改邀请的状态
     *
     * @param id               邀请ID
     * @param invitationStatus 邀请状态
     * @return 剩余待处理邀请
     */
    public Map<String, Object> changeInvitationStatus(String id, String invitationStatus, String[] topics) {
        Invitation invitation = repositoryService.findByInvitationId(id); //找到对应的邀请
        invitation.setInvitationStatus(invitationStatus); //更改邀请的状态
        if (invitationStatus.equals("pass")) { //通过邀请，被邀请人成为pcMember
            // 将受邀请人变为pcMember
            User user = repositoryService.findByUserId(invitation.getInvitee());
            Meeting meeting = repositoryService.findByMeetingId(invitation.getMeetingId());
            repositoryService.addUserAndMeetingAuthorities(user, meeting, "ROLE_PCMEMBER");
            //添加topic的关联项
            List<TopicPcMeetingArticle> allRelation = new ArrayList<>();
            for (String s : topics) {
                allRelation.addAll(topicPcMeetingArticleRepository.findByMeetingAndTopic(meeting.getId(), s));
            } //找到所有的关联项
            for (TopicPcMeetingArticle r : allRelation) {
                if (topicPcMeetingArticleRepository.findByMeetingAndTopicAndPc(r.getMeeting().getId(), r.getTopic().getId(), user.getId()) == null) {
                    TopicPcMeetingArticle newRelation = new TopicPcMeetingArticle(r.getTopic(), user, r.getMeeting());
                    topicPcMeetingArticleRepository.save(newRelation);
                }
            }
        }
        invitationRepository.save(invitation);
        return invitationIReceived(id);
    }
}
