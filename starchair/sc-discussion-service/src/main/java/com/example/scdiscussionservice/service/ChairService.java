package com.example.scdiscussionservice.service;

import com.example.scdiscussionservice.domain.Article;
import com.example.scdiscussionservice.domain.Meeting;
import com.example.scdiscussionservice.domain.Score;
import com.example.scdiscussionservice.repository.ArticleRepository;
import com.example.scdiscussionservice.repository.Main_DiscussionRepository;
import com.example.scdiscussionservice.repository.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class ChairService {
    private MeetingRepository meetingRepository;
    private RepositoryService repositoryService;
    private ArticleRepository articleRepository;
    private Main_DiscussionRepository mainDiscussionRepository;
    private Random rand = new Random();

    @Autowired
    public ChairService(MeetingRepository meetingRepository,
                        RepositoryService repositoryService,
                        ArticleRepository articleRepository,
                        Main_DiscussionRepository mainDiscussionRepository) {
        this.meetingRepository = meetingRepository;
        this.repositoryService = repositoryService;
        this.articleRepository = articleRepository;
        this.mainDiscussionRepository = mainDiscussionRepository;
    }


    /**
     * 查看该会议所有稿件
     *
     * @param meetingId 会议Id
     * @return article list
     */
    public List<Article> getAllArticles(String meetingId) {
        Meeting meeting = repositoryService.findByMeetingId(meetingId);
        List<Article> articleList = articleRepository.findByMeetingId(meetingId);
        if (meeting.getSubmitStatus().equals("withTopic") || meeting.getSubmitStatus().equals("withAverage")) {
            for (Article article : articleList) {
                Set<Score> scores = article.getScores();
                int count = 0;
                for (Score score : scores) {
                    if (score.getReviewStatus() != null) {
                        count++;
                    }
                }
                if (count == 3) {
                    article.setReviewStatus("Reviewed");
                    articleRepository.save(article);
                }
            }
        }
        return articleList;
    }


}
