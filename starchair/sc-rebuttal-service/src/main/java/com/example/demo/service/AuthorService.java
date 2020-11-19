package com.example.demo.service;


import com.example.demo.domain.Article;
import com.example.demo.domain.Score;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.request.RebuttalRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AuthorService {
    private ArticleRepository articleRepository;


    @Autowired
    public AuthorService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 作者rebuttal，修改article的reviewStatus属性，存reason,开始二轮讨论
     *
     * @param rebuttalRequest reason,username,meetingId,articleId
     * @return success
     */
    public String authorRebuttal(RebuttalRequest rebuttalRequest) {
        String isRebuttaled = rebuttalRequest.getIsRebuttaled();
        Article article = articleRepository.findByArticleId(Long.parseLong(rebuttalRequest.getArticleId()));
        if (isRebuttaled.equals("false")) {
            article.setReviewStatus("Confirmed");
            articleRepository.save(article);
        } else if (isRebuttaled.equals("true")) {
            article.setReviewStatus("Rebuttaled");
            article.setRebuttal(rebuttalRequest.getReason());
            Set<Score> scoreSet = article.getScores();
            for (Score score : scoreSet) {
                score.setModifyStatus(null);
            }
            article.setScores(scoreSet);
            articleRepository.save(article);
        }
        return "success";
    }
}
