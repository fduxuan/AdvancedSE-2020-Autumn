package com.example.seendexaminenemanuscripts.service;


import com.example.seendexaminenemanuscripts.domain.*;
import com.example.seendexaminenemanuscripts.exception.ReviewNotFinishedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.seendexaminenemanuscripts.repository.ArticleRepository;
import com.example.seendexaminenemanuscripts.repository.Main_DiscussionRepository;
import com.example.seendexaminenemanuscripts.repository.MeetingRepository;

import java.util.*;

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

    /**
     * 结束评审，修改会议的submitStatus，如果评审还未结束，抛出异常
     *
     * @param meetingId 会议Id
     * @return 是否成功
     */
    public String finishReview(String meetingId) {
        List<Article> articleList = articleRepository.findByMeetingId(meetingId);
        boolean finished = true;
        for (Article article : articleList) {
            if (article.getReviewStatus() == null) {
                finished = false;
                break;
            }
        }
        if (!finished) {
            throw new ReviewNotFinishedException();
        }
        Meeting meeting = repositoryService.findByMeetingId(meetingId);
        meeting.setSubmitStatus("firstDiscussion");
        meetingRepository.save(meeting);
        for (Article article : articleList) { //建每个article的讨论贴第一条
            Main_Discussion mainDiscussion = new Main_Discussion(article.getId().toString(), meeting.getApplicant(), null);
            mainDiscussion.setContent("Feel free to share your precious opinions on this paper.");
            mainDiscussionRepository.save(mainDiscussion);
        }
        return "success";
    }
}
