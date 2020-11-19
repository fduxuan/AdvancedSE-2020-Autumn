package com.example.scdiscussionservice.service;

import com.example.scdiscussionservice.controller.request.ModifyScoreRequest;
import com.example.scdiscussionservice.controller.request.ScoreRequest;
import com.example.scdiscussionservice.domain.Article;
import com.example.scdiscussionservice.domain.Score;
import com.example.scdiscussionservice.domain.User;
import com.example.scdiscussionservice.exception.NoDiscussionException;
import com.example.scdiscussionservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PcMemberService {
    private UserRepository userRepository;
    private ArticleRepository articleRepository;
    private ScoreRepository scoreRepository;
    private Main_DiscussionRepository mainDiscussionRepository;
    private Sub_DiscussionRepository subDiscussionRepository;
    private RepositoryService repositoryService;

    @Autowired
    public PcMemberService(UserRepository userRepository,
                           ScoreRepository scoreRepository,
                           ArticleRepository articleRepository,
                           Main_DiscussionRepository mainDiscussionRepository,
                           Sub_DiscussionRepository subDiscussionRepository,
                           RepositoryService repositoryService) {
        this.userRepository = userRepository;
        this.articleRepository = articleRepository;
        this.scoreRepository = scoreRepository;
        this.mainDiscussionRepository = mainDiscussionRepository;
        this.subDiscussionRepository = subDiscussionRepository;
        this.repositoryService = repositoryService;
    }

    /**
     * 获取pcmember在指定会议分配到的article的具体信息
     *
     * @param username  pcmember的用户名
     * @param meetingId 会议Id
     * @return article list
     */
    /**
     * 存储指定article的score
     *
     * @return 是否成功
     */
    public String submitScore(ScoreRequest scoreRequest) {
        User pc = userRepository.findByUsername(scoreRequest.getUsername());
        Score score = scoreRepository.findScoreByPcAndArticleId(pc.getId(), Long.parseLong(scoreRequest.getArticleId()));
        score.setScore(scoreRequest.getScore());
        score.setComment(scoreRequest.getComment());
        score.setConfidence(scoreRequest.getConfidence());
        score.setReviewStatus("reviewed");
        scoreRepository.save(score);
        return "success";
    }

    /**
     * 修改article的评分
     * 如果还没有在讨论中发言，抛出异常
     *
     * @param modifyScoreRequest 新评分信息
     * @return 是否成功
     */
    public String submitModify(ModifyScoreRequest modifyScoreRequest) {
        User pc = userRepository.findByUsername(modifyScoreRequest.getUsername());
        String articleId = modifyScoreRequest.getArticleId();
        Article article = articleRepository.findByArticleId(Long.parseLong(articleId));
        if (article.getReviewStatus().equals("Reviewed")) {
            if (mainDiscussionRepository.findMainByUsernameAndArticleIdAndDiscussStatus(pc.getUsername(), articleId, "firstDiscussion") == null &&
                    subDiscussionRepository.findSubByUsernameAndArticleIdAndDiscussStatus(pc.getUsername(), articleId, "firstDiscussion") == null) {//pc未参加讨论
                throw new NoDiscussionException();
            }
        } else if (article.getReviewStatus().equals("Rebuttaled")) {
            if (mainDiscussionRepository.findMainByUsernameAndArticleIdAndDiscussStatus(pc.getUsername(), articleId, "secondDiscussion") == null &&
                    subDiscussionRepository.findSubByUsernameAndArticleIdAndDiscussStatus(pc.getUsername(), articleId, "secondDiscussion") == null) {//pc未参加讨论
                throw new NoDiscussionException();
            }
        }
        String modifyStatus = modifyScoreRequest.getModifyStatus();
        Score score = scoreRepository.findScoreByPcAndArticleId(pc.getId(), Long.parseLong(articleId)); //原来的分数

        if (article.getReviewStatus().equals("Rebuttaled")) {
            Score oldScore = new Score(score.getPcmember_id(), score.getScore(), score.getComment(), score.getConfidence(),
                    score.getReviewStatus(), score.getId().toString());
            scoreRepository.save(oldScore);
        }

        if (modifyStatus.equals("confirmed")) { //确认分数
            score.setModifyStatus(modifyStatus);
            scoreRepository.save(score);
            return "success";
        } else { //修改分数
            score.setScore(modifyScoreRequest.getScore());
            score.setComment(modifyScoreRequest.getComment());
            score.setConfidence(modifyScoreRequest.getConfidence());
            score.setModifyStatus(modifyStatus);
            scoreRepository.save(score);
            return "success";
        }
    }
}
