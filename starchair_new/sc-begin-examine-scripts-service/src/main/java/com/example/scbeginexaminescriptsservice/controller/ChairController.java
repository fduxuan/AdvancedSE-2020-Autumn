package com.example.scbeginexaminescriptsservice.controller;

import com.example.scbeginexaminescriptsservice.domain.Article;
import com.example.scbeginexaminescriptsservice.service.ChairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ChairController {
    private ChairService chairService;

    @Autowired
    public ChairController(ChairService chairService) {
        this.chairService = chairService;
    }


    @GetMapping("/getPcList") //查找指定会议的pcMember
    @PreAuthorize("hasRole('ROLE_CHAIR')")
    public ResponseEntity<Map<String, Object>> getPcList(@RequestParam("meetingID") String meetingID) {
        return ResponseEntity.ok(chairService.getPcList(meetingID));
    }

    @GetMapping("/startReview") //开启审稿
    @PreAuthorize("hasRole('ROLE_CHAIR')")
    public ResponseEntity<Map<String, String>> startReview(@RequestParam("meetingId") String meetingId,
                                         @RequestParam("strategy") String strategy) {
        Map<String, String> response = new HashMap<>();
        response.put("message", chairService.allotArticle(meetingId, strategy));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewAllArticles") //查看所有稿件信息
    @PreAuthorize("hasRole('ROLE_CHAIR')")
    public ResponseEntity<List<Article>> viewAllArticles(@RequestParam("meetingId") String meetingId) {
        return ResponseEntity.ok(chairService.getAllArticles(meetingId));
    }

}
