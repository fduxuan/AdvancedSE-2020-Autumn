package com.example.scmodifyarticleinfoservice.service;


import com.example.scmodifyarticleinfoservice.domain.Article;
import com.example.scmodifyarticleinfoservice.repository.ArticleRepository;
import com.example.scmodifyarticleinfoservice.repository.AuthorRepository;
import com.example.scmodifyarticleinfoservice.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthorService {
    private ArticleRepository articleRepository;
    private AuthorRepository authorRepository;
    private TopicRepository topicRepository;
    private AllMeetingsService allMeetingsService;

    @Autowired
    public AuthorService(ArticleRepository articleRepository,
                         AuthorRepository authorRepository,
                         TopicRepository topicRepository,
                         AllMeetingsService allMeetingsService) {
        this.articleRepository = articleRepository;
        this.authorRepository = authorRepository;
        this.topicRepository = topicRepository;
        this.allMeetingsService = allMeetingsService;
    }


    /**
     * 删除原投稿，修改稿件
     *
     * @return 是否成功信息
     */
    public String modifyArticleContribution(MultipartFile file, String articleName,
                                            String author, String summary, String[] topics,
                                            String username, String meetingId, String articleId, String parentDir) {
        Article oldArticle = articleRepository.findByArticleId(Long.parseLong(articleId));
        authorRepository.deleteArticleAuthor(oldArticle.getId());
        topicRepository.deleteArticleTopic(oldArticle.getId());
        articleRepository.deleteArticle(oldArticle.getId());
        return allMeetingsService.storeArticleContribution(file, articleName, author,
                summary, topics, username, meetingId, parentDir);
    }
}
