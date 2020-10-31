package com.example.seendexaminenemanuscripts.controller;

import com.example.seendexaminenemanuscripts.domain.Article;
import com.example.seendexaminenemanuscripts.service.ChairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class EndExamineManuscriptsController {
    private ChairService chairService;

    @GetMapping("/viewAllArticles") //查看所有稿件信息
    public ResponseEntity<List<Article>> viewAllArticles(@RequestParam("meetingId") String meetingId) {
        return ResponseEntity.ok(chairService.getAllArticles(meetingId));
    }

    @GetMapping("/finishReview") //结束评审，创建讨论贴
    public ResponseEntity<String> finishReview(@RequestParam("meetingId") String meetingId) {
        return ResponseEntity.ok(chairService.finishReview(meetingId));
    }

}
