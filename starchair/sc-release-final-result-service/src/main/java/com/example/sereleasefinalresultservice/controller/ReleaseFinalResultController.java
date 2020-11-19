package com.example.sereleasefinalresultservice.controller;

import com.example.sereleasefinalresultservice.service.ChairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReleaseFinalResultController {
    private ChairService chairService;

    @GetMapping("/finalPublishScores") //发布评审结果
    public ResponseEntity<String> finalPublishScores(@RequestParam("meetingId") String meetingId) {
        return ResponseEntity.ok(chairService.finalPublishScores(meetingId));
    }
}
