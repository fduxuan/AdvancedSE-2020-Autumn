package com.example.scmodifyarticleinfoservice.controller;


import com.example.scmodifyarticleinfoservice.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthorController {
    private AuthorService authorService;

    @PostMapping("/modifyAuthor") //修改投稿信息
    public ResponseEntity<Map<String, String>> modifyArticleContribution(@RequestParam("templateFile") MultipartFile file,
                                                                         @RequestParam("articleName") String articleName,
                                                                         @RequestParam("author") String author,
                                                                         @RequestParam("summary") String summary,
                                                                         @RequestParam("topics") String[] topics,
                                                                         @RequestParam("username") String username,
                                                                         @RequestParam("meetingId") String meetingId,
                                                                         @RequestParam("articleId") String articleId,
                                                                         HttpServletRequest request) {
        String parentDir = request.getServletContext().getRealPath("src");
        Map<String, String> response = new HashMap<>();
        response.put("message", authorService.modifyArticleContribution(file, articleName, author,
                summary, topics, username, meetingId, articleId, parentDir));
        return ResponseEntity.ok(response);
    }
}
