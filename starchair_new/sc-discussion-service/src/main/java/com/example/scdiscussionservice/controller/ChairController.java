package com.example.scdiscussionservice.controller;

import com.example.scdiscussionservice.domain.Article;
import com.example.scdiscussionservice.service.ChairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ChairController {
    private ChairService chairService;

    @Autowired
    public ChairController(ChairService chairService) {
        this.chairService = chairService;
    }

    @GetMapping("/viewAllArticles") //查看所有稿件信息
    @PreAuthorize("hasRole('ROLE_CHAIR')")
    public ResponseEntity<List<Article>> viewAllArticles(@RequestParam("meetingId") String meetingId) {
        return ResponseEntity.ok(chairService.getAllArticles(meetingId));
    }


}
