package com.example.scadminmanageconferenceservice.service;


import com.example.scadminmanageconferenceservice.controller.request.ChangeApplicationStatusRequest;
import com.example.scadminmanageconferenceservice.domain.Meeting;
import com.example.scadminmanageconferenceservice.domain.TopicPcMeetingArticle;
import com.example.scadminmanageconferenceservice.domain.User;
import com.example.scadminmanageconferenceservice.repository.MeetingRepository;
import com.example.scadminmanageconferenceservice.repository.TopicPcMeetingArticleRepository;
import com.example.scadminmanageconferenceservice.repository.TopicRepository;
import com.example.scadminmanageconferenceservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConferenceApplicationService {
    private MeetingRepository meetingRepository;
    private UserRepository userRepository;
    private RepositoryService repositoryService;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    private TopicRepository topicRepository;


    /**
     * 管理员页面返回待审核会议
     *
     * @return 待审核会议列表
     */
    public List<Object> getUncheckedConference() {
        List<Meeting> uncheckedMeeting = meetingRepository.findByIsVarified("false");
        return repositoryService.getMeetingAndTopicList(uncheckedMeeting);
    }

    /**
     * 通过ID找到具体要操作的会议
     * 修改会议isVarified状态，表示审核通过或不通过
     * 如果审核通过，申请人变为该会议chair
     * 返回新的待审核会议的list
     *
     * @param request 会议ID和审核是否通过status
     */
    public List<Meeting> changeApplicationStatus(ChangeApplicationStatusRequest request) {
        String applicationId = request.getApplicationId();
        String applicationStatus = request.getApplyStatus();
        Meeting meeting = repositoryService.findByMeetingId(applicationId); // 找到该会议
        meeting.setIsVarified(applicationStatus); // 修改会议审核状态
        if (applicationStatus.equals("pass")) { // 申请通过
            String applicant = meeting.getApplicant();
            User user = userRepository.findByUsername(applicant); // 找到申请人
            repositoryService.addUserAndMeetingAuthorities(user, meeting, "ROLE_CHAIR");
            repositoryService.addUserAndMeetingAuthorities(user, meeting, "ROLE_PCMEMBER");
            //添加topic的关联项
            List<TopicPcMeetingArticle> allRelation = topicPcMeetingArticleRepository.findByMeetingId(meeting.getId());
            for (TopicPcMeetingArticle r : allRelation) {
                r.setPcmember(user);
                topicPcMeetingArticleRepository.save(r);
            }
        }
        meetingRepository.save(meeting);
        return meetingRepository.findByIsVarified("false"); // 返回新的待审核会议list
    }
}
