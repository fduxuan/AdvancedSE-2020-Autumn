package com.example.screleasefirstresultservice.service;


import com.example.screleasefirstresultservice.domain.Article;
import com.example.screleasefirstresultservice.domain.Meeting;
import com.example.screleasefirstresultservice.domain.Score;
import com.example.screleasefirstresultservice.domain.User;
import com.example.screleasefirstresultservice.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PcMemberService {
    private UserRepository userRepository;
    private ArticleRepository articleRepository;
    private ScoreRepository scoreRepository;
    private RepositoryService repositoryService;

    /**
     * 获取pcmember在指定会议分配到的article的具体信息
     *
     * @param username  pcmember的用户名
     * @param meetingId 会议Id
     * @return article list
     */
    public Map<String,Object> getAllotedArticle(String username, String meetingId) {
        User pc = userRepository.findByUsername(username);
        Meeting meeting = repositoryService.findByMeetingId(meetingId);
        List<Article> articleList = articleRepository.getAllotedArticle(pc.getId(), meetingId);
        ListIterator<Article> articleListIterator = articleList.listIterator();
        while (articleListIterator.hasNext()) {
            Article article = articleListIterator.next();
            Set<Score> scoreSet = article.getScores();
            scoreSet.removeIf(score -> !score.getPcmember_id().equals(pc.getId()));
            article.setScores(scoreSet);
            articleListIterator.set(article);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("articleList",articleList);
        map.put("submitStatus",meeting.getSubmitStatus());
        return map;
    }

}
