package com.example.scmodifyarticleinfoservice.service;


import com.alibaba.fastjson.JSON;
import com.example.scmodifyarticleinfoservice.domain.Article;
import com.example.scmodifyarticleinfoservice.domain.Author;
import com.example.scmodifyarticleinfoservice.domain.Meeting;
import com.example.scmodifyarticleinfoservice.domain.User;
import com.example.scmodifyarticleinfoservice.exception.ArticleHasBeenContributedException;
import com.example.scmodifyarticleinfoservice.repository.ArticleRepository;
import com.example.scmodifyarticleinfoservice.repository.AuthorityRepository;
import com.example.scmodifyarticleinfoservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
public class AllMeetingsService {
    private Logger logger = LoggerFactory.getLogger(AllMeetingsService.class);
    private UserRepository userRepository;
    private AuthorityRepository authorityRepository;
    private ArticleRepository articleRepository;
    private RepositoryService repositoryService;

    @Autowired
    public AllMeetingsService(
                              UserRepository userRepository,
                              AuthorityRepository authorityRepository,
                              ArticleRepository articleRepository,
                              RepositoryService repositoryService) {
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.articleRepository = articleRepository;
        this.repositoryService = repositoryService;
    }

    /**
     * 存储会议投稿到投稿数据库，添加投稿人为author
     *
     * @param file        投稿文件
     * @param articleName 文章名称
     * @param username    投稿人用户名
     * @param meetingId   投稿会议Id
     * @return message 是否投稿成功
     */
    public String storeArticleContribution(MultipartFile file, String articleName,
                                           String author, String summary, String[] topics,
                                           String username, String meetingId, String parentDir) {
        if (articleRepository.getArticle(articleName, summary, username, meetingId) != null) {
            throw new ArticleHasBeenContributedException();
        }
        User user = userRepository.findByUsername(username);
        Meeting meeting = repositoryService.findByMeetingId(meetingId);
        //先判断该用户是否在该会议投过稿，投过稿则不添加新的author角色，直接存储到投稿数据库
        if (authorityRepository.findByAuthorAndUserIdAndMeetingId("ROLE_AUTHOR", user.getId(), meeting.getId()) == null) {
            //没投过要新建author角色
            repositoryService.addUserAndMeetingAuthorities(user, meeting, "ROLE_AUTHOR");
        }
        String filePath = saveFile(file, parentDir);
        if (!filePath.equals("error")) {
            Article article = new Article(articleName, summary, filePath, username, meetingId, "false");
            articleRepository.save(article);
            Article newArticle = articleRepository.getArticle(article.getArticleName(), article.getSummary(), article.getContributor(), article.getMeetingId());
            newArticle = repositoryService.setArticleTopic(newArticle, topics); //设置article的topic
            //设置author
            List<Author> authorList = JSON.parseArray(author, Author.class);
            repositoryService.setArticleAuthor(newArticle, authorList);
            return "success";
        } else return "error";
    }

    /**
     * 将文件存储到服务器src/main/file目录下
     *
     * @param file 要存储的文件
     * @return 文件的路径
     */
    private String saveFile(MultipartFile file, String parentDir) {
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename(); //获取文件名
            try {
                assert fileName != null;
                String suffixName = fileName.substring(fileName.lastIndexOf('.')); //获取文件后缀
                fileName = UUID.randomUUID() + suffixName; //重新生成文件名
                File dir = new File(parentDir + "/main/file");
                if (!dir.exists()) {
                    dir.mkdirs(); //创建文件存储根目录
                }
                String filePath = parentDir + "/main/file/" + fileName;
                File f = new File(filePath);
                file.transferTo(f); //将文件存储到本地
                return filePath;
            } catch (Exception e) {
                logger.debug("Content", e);
                return "error";
            }
        } else {
            logger.debug("file is empty");
            return "error";
        }
    }
}
