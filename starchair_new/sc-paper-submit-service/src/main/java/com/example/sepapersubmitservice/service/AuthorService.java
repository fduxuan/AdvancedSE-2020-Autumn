package com.example.sepapersubmitservice.service;

import com.example.sepapersubmitservice.domain.Article;
import com.example.sepapersubmitservice.domain.Meeting;
import com.example.sepapersubmitservice.domain.Score;
import com.example.sepapersubmitservice.repository.ArticleRepository;
import com.example.sepapersubmitservice.repository.AuthorRepository;
import com.example.sepapersubmitservice.repository.ScoreRepository;
import com.example.sepapersubmitservice.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class AuthorService {
    private ArticleRepository articleRepository;
    private RepositoryService repositoryService;
    private AuthorRepository authorRepository;
    private TopicRepository topicRepository;
    private ScoreRepository scoreRepository;
    private AllMeetingsService allMeetingsService;

    @Autowired
    public AuthorService(ArticleRepository articleRepository,
                         RepositoryService repositoryService,
                         AuthorRepository authorRepository,
                         TopicRepository topicRepository,
                         ScoreRepository scoreRepository,
                         AllMeetingsService allMeetingsService) {
        this.articleRepository = articleRepository;
        this.repositoryService = repositoryService;
        this.authorRepository = authorRepository;
        this.topicRepository = topicRepository;
        this.allMeetingsService = allMeetingsService;
        this.scoreRepository = scoreRepository;
    }

    /**
     * 获取当前用户在当前会议的投稿详情
     *
     * @param username  用户名
     * @param meetingId 会议Id
     * @return 投稿详情list
     */
    public Map<String, Object> getContribution(String username, String meetingId) {
        Map<String, Object> map = new HashMap<>();
        List<Article> articleList = articleRepository.findByContributorAndMeetingId(username, meetingId);
        Meeting meeting = repositoryService.findByMeetingId(meetingId);
        if (meeting.getSubmitStatus().equals("insubmit") ||
                meeting.getSubmitStatus().equals("withTopic") ||
                meeting.getSubmitStatus().equals("withBurden") ||
                meeting.getSubmitStatus().equals("firstDiscussion")) { //如果还没有发布评审结果，score属性为null
            for (Article article : articleList) {
                article.setScores(null);
            }
        } else if (meeting.getSubmitStatus().equals("rebuttal")) {
            for (Article article : articleList) {
                if (article.getReviewStatus().equals("Rebuttaled")) {
                    article.setScores(substituteWithOldScore(article));
                }
            }
        }

        map.put("articleDetails", articleList);
        map.put("topicDetails", repositoryService.getTopic(meetingId));
        return map;
    }

    private Set<Score> substituteWithOldScore(Article article) {
        List<Score> scoreList = new ArrayList<>(article.getScores());
        ListIterator<Score> scoreListIterator = scoreList.listIterator();
        while (scoreListIterator.hasNext()) {
            Score score = scoreListIterator.next();
            if (score.getModifyStatus() != null && score.getModifyStatus().equals("modified")) {
                Score oldScore = scoreRepository.findScoreByOldId(score.getId().toString());
                scoreListIterator.remove();
                scoreListIterator.add(oldScore);
            }
        }
        return new HashSet<>(scoreList);
    }

}
