package com.example.sereleasefinalresultservice.service;


import com.example.sereleasefinalresultservice.domain.Article;
import com.example.sereleasefinalresultservice.domain.Meeting;
import com.example.sereleasefinalresultservice.domain.Score;
import com.example.sereleasefinalresultservice.exception.ScoreNotConfirmedException;
import com.example.sereleasefinalresultservice.repository.ArticleRepository;
import com.example.sereleasefinalresultservice.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChairService {
    private MeetingRepository meetingRepository;
    private RepositoryService repositoryService;
    private ArticleRepository articleRepository;
    private Random rand = new Random();

    @Autowired
    public ChairService(MeetingRepository meetingRepository,
                        RepositoryService repositoryService,
                        ArticleRepository articleRepository) {
        this.meetingRepository = meetingRepository;
        this.repositoryService = repositoryService;
        this.articleRepository = articleRepository;

    }

    /**
     * 发布最终评审结果，修改会议的submitStatus
     *
     * @param meetingId 会议Id
     * @return 是否成功
     */
    public String finalPublishScores(String meetingId) {
        List<Article> articleList = articleRepository.findByMeetingId(meetingId);
        for (Article article : articleList) { //判断分数是否都已确认
            Set<Score> scoreList = article.getScores();
            for (Score score : scoreList) {
                if (score.getModifyStatus() == null) {
                    throw new ScoreNotConfirmedException();
                }
            }
        }
        checkIfAccepted(articleList); //判断是否被录用
        Meeting meeting = repositoryService.findByMeetingId(meetingId);
        meeting.setSubmitStatus("final");
        meetingRepository.save(meeting);
        return "success";
    }

    /**
     * 判断article是否被录用，修改article的isAccepted属性，录用为true，否则为false
     *
     * @param articleList articleList
     */
    private void checkIfAccepted(List<Article> articleList) {
        for (Article article : articleList) {
            Set<Score> scores = article.getScores();
            String isAccepted = "true";
            for (Score score : scores) {
                if (score.getScore().equals("-1") || score.getScore().equals("-2")) {
                    isAccepted = "false";
                    break;
                }
            }
            article.setIsAccepted(isAccepted);
            articleRepository.save(article);
        }
    }
}
