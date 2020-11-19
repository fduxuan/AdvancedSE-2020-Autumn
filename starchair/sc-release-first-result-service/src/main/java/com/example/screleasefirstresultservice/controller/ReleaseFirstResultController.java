package com.example.screleasefirstresultservice.controller;


import com.example.screleasefirstresultservice.service.ChairService;
import com.example.screleasefirstresultservice.service.PcMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ReleaseFirstResultController {
    private ChairService chairService;

    private PcMemberService pcMemberService;

    @GetMapping("/getAllotedArticle") //查看分配到的稿件信息
    @PreAuthorize("hasRole('ROLE_PCMEMBER')")
    public ResponseEntity<Map<String,Object>> getAllotedArticle(@RequestParam("username") String username,
                                                                @RequestParam("meetingId") String meetingId) {
        return ResponseEntity.ok(pcMemberService.getAllotedArticle(username, meetingId));
    }

    @GetMapping("/firstPublishScores") //发布评审结果
    @PreAuthorize("hasRole('ROLE_CHAIR')")
    public ResponseEntity<String> firstPublishScores(@RequestParam("meetingId") String meetingId) {
        return ResponseEntity.ok(chairService.firstPublishScores(meetingId));
    }
}
