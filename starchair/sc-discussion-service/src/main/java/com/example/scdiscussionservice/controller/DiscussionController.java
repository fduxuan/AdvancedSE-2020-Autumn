package com.example.scdiscussionservice.controller;

import com.example.scdiscussionservice.controller.request.NewCommentRequest;
import com.example.scdiscussionservice.controller.request.NewPostRequest;
import com.example.scdiscussionservice.controller.request.ReplyCommentRequest;
import com.example.scdiscussionservice.domain.Main_Discussion;
import com.example.scdiscussionservice.service.DiscussionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DiscussionController {
    private DiscussionService discussionService;

    @Autowired
    public DiscussionController(DiscussionService discussionService) {
        this.discussionService = discussionService;
    }

    @GetMapping("/discussion") //获取讨论页面数据
    public ResponseEntity<List<Main_Discussion>> getArticleDiscussion(@RequestParam("articleId") String articleId) {
        return ResponseEntity.ok(discussionService.getArticleDiscussion(articleId));
    }

    @PostMapping("/newPost") //回贴
    public ResponseEntity<List<Main_Discussion>> sendNewPost(@RequestBody NewPostRequest newPostRequest) {
        return ResponseEntity.ok(discussionService.sendNewPost(newPostRequest));
    }

    @PostMapping("/newComment") //回复
    public ResponseEntity<List<Main_Discussion>> sendNewComment(@RequestBody NewCommentRequest newCommentRequest) {
        return ResponseEntity.ok(discussionService.sendNewComment(newCommentRequest));
    }

    @PostMapping("/replyComment") //回复回复
    public ResponseEntity<List<Main_Discussion>> replyComment(@RequestBody ReplyCommentRequest replyCommentRequest) {
        return ResponseEntity.ok(discussionService.replyComment(replyCommentRequest));
    }
}