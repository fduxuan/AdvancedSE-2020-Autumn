package com.example.seconfirmfinishservice.controller;

import com.example.seconfirmfinishservice.service.ChairService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ConfirmFinishController {
    private ChairService chairService;
    @GetMapping("/confirmFinish") //无投稿但确认结束投稿
    public ResponseEntity<Map<String, String>> confirmFinish(@RequestParam("meetingId") String meetingId) {
        Map<String, String> response = new HashMap<>();
        response.put("message", chairService.finishContribution(meetingId));
        return ResponseEntity.ok(response);
    }
}
