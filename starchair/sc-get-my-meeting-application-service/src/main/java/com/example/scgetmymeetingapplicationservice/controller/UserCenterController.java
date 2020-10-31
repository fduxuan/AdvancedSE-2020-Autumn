package com.example.scgetmymeetingapplicationservice.controller;

import com.example.scgetmymeetingapplicationservice.service.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserCenterController {
    private UserCenterService userCenterService;

    @Autowired
    public UserCenterController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }

    @GetMapping("/myApplication") //我申请的所有会议
    public ResponseEntity<List<Object>> getMyMeetingApplication(@RequestParam("username") String username) {
        return ResponseEntity.ok(userCenterService.myMeetingApplication(username));
    }
}
