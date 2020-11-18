package com.example.scinvitepcservice.service;


import com.example.scinvitepcservice.domain.Invitation;
import com.example.scinvitepcservice.domain.User;
import com.example.scinvitepcservice.repository.InvitationRepository;
import com.example.scinvitepcservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ChairService {
    private UserRepository userRepository;
    private InvitationRepository invitationRepository;
    private Random rand = new Random();

    @Autowired
    public ChairService(
                        UserRepository userRepository,
                        InvitationRepository invitationRepository
                        ) {
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
    }

    /**
     * 用用户的真实姓名查找所有用户
     *
     * @param fullName 用户的真实姓名
     * @return 用户列表
     */
    public List<User> searchForUser(String fullName) {
        return userRepository.findByFullName(fullName);
    }

    /**
     * 发送邀请到被邀请用户,相同邀请只存一次
     *
     * @param meetingID 会议ID
     * @param inviter   邀请人
     * @param invitee   被邀请人ID数组
     * @return message 是否成功
     */
    public String sendInvitation(String meetingID, String inviter, String[] invitee) {
        for (String invited : invitee) {
            User user = userRepository.findByUsername(inviter);
            Optional<Invitation> optionalInvitation = invitationRepository.findByInviterAndInviteeAndMeetingID(inviter, invited, meetingID);
            if (!optionalInvitation.isPresent() && !user.getId().toString().equals(invited)) {
                Invitation invitation = new Invitation(inviter, invited, meetingID, "false");
                invitationRepository.save(invitation);
            }
        }
        return "success";
    }

}
