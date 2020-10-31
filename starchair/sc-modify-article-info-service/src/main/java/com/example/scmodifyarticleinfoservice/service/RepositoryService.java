package com.example.scmodifyarticleinfoservice.service;

import com.example.scmodifyarticleinfoservice.domain.*;
import com.example.scmodifyarticleinfoservice.exception.MeetingNotFoundException;
import com.example.scmodifyarticleinfoservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RepositoryService {
    private UserRepository userRepository;
    private MeetingRepository meetingRepository;
    private AuthorityRepository authorityRepository;
    private InvitationRepository invitationRepository;
    private TopicRepository topicRepository;
    private AuthorRepository authorRepository;
    private Sub_DiscussionRepository subDiscussionRepository;

    @Autowired
    public RepositoryService(UserRepository userRepository,
                             MeetingRepository meetingRepository,
                             AuthorityRepository authorityRepository,
                             TopicRepository topicRepository,
                             AuthorRepository authorRepository,
                             InvitationRepository invitationRepository,
                             Sub_DiscussionRepository subDiscussionRepository) {
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.authorityRepository = authorityRepository;
        this.invitationRepository = invitationRepository;
        this.topicRepository = topicRepository;
        this.authorRepository = authorRepository;
        this.subDiscussionRepository = subDiscussionRepository;
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
     * 设置article的topics
     *
     * @param article article
     * @param topics  对应的topic
     * @return article
     */
    Article setArticleTopic(Article article, String[] topics) {
        for (String s : topics) {
            Topic topic = new Topic(s);
            article.getTopics().add(topic);
            topic.setArticle(article);
            topicRepository.save(topic);
        }
        return article;
    }

    Article setArticleAuthor(Article article, List<Author> authorList) {
        for (int i = 0; i < authorList.size(); i++) {
            Author newAuthor = authorList.get(i);
            newAuthor.setOrderOfAuthor(i);
            article.getAuthors().add(newAuthor);
            newAuthor.setArticle1(article);
            authorRepository.save(newAuthor);
        }
        return article;
    }

}
