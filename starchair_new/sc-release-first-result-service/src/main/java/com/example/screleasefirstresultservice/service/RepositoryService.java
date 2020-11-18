package com.example.screleasefirstresultservice.service;


import com.example.screleasefirstresultservice.domain.*;
import com.example.screleasefirstresultservice.exception.MeetingNotFoundException;
import com.example.screleasefirstresultservice.repository.*;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class RepositoryService {
    private UserRepository userRepository;
    private MeetingRepository meetingRepository;
    private AuthorityRepository authorityRepository;
    private ScoreRepository scoreRepository;

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
