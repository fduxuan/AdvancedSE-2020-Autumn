package com.example.scbeginexaminescriptsservice.service;

import com.example.scbeginexaminescriptsservice.domain.*;
import com.example.scbeginexaminescriptsservice.exception.MeetingNotFoundException;
import com.example.scbeginexaminescriptsservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RepositoryService {
    private UserRepository userRepository;
    private MeetingRepository meetingRepository;
    private AuthorityRepository authorityRepository;
    private TopicPcMeetingArticleRepository topicPcMeetingArticleRepository;
    private ScoreRepository scoreRepository;

    @Autowired
    public RepositoryService(UserRepository userRepository,
                             MeetingRepository meetingRepository,
                             AuthorityRepository authorityRepository,
                             TopicPcMeetingArticleRepository topicPcMeetingArticleRepository,
                            ScoreRepository scoreRepository){
        this.userRepository = userRepository;
        this.meetingRepository = meetingRepository;
        this.authorityRepository = authorityRepository;
        this.topicPcMeetingArticleRepository = topicPcMeetingArticleRepository;
        this.scoreRepository = scoreRepository;
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

    /**
     * 获取某个指定会议的topic list
     *
     * @param meetingId 会议id
     * @return 该会议的topic
     */
    List<Topic> getTopic(String meetingId) {
        Meeting meeting = findByMeetingId(meetingId);
        List<Topic> topics = new ArrayList<>();
        List<TopicPcMeetingArticle> relation = topicPcMeetingArticleRepository.findByMeetingId(meeting.getId());
        for (TopicPcMeetingArticle rel : relation) {
            if (!topics.contains(rel.getTopic()))
                topics.add(rel.getTopic());
        }
        return topics;
    }


    /**
     * 设置article与pc的对应关系
     *
     * @param article article
     * @param pc      对应的pc
     */
    void allotArticleToPc(Article article, User pc) {
        Set<Article> tempArticle = new HashSet<>();
        Set<User> tempPc = new HashSet<>();
        Set<Score> tempScore = new HashSet<>();
        if (pc.getArticles() != null) {
            tempArticle = pc.getArticles();
        }
        tempArticle.add(article);
        pc.setArticles(tempArticle);
        if (article.getPcmembers() != null) {
            tempPc = article.getPcmembers();
        }
        tempPc.add(pc);
        Score score = new Score(pc.getId());
        if (article.getScores() != null) {
            tempScore = article.getScores();
        }
        tempScore.add(score);
        article.setScores(tempScore);
        article.setPcmembers(tempPc);
        article.setReviewStatus("Reviewing");
        score.setArticle2(article);
        scoreRepository.save(score);
    }

}
