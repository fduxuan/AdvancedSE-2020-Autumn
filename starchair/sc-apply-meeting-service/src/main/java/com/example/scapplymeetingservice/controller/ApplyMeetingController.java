package com.example.scapplymeetingservice.controller;

import com.example.scapplymeetingservice.controller.request.MeetingRequest;
;
import com.example.scapplymeetingservice.domain.Meeting;
import com.example.scapplymeetingservice.service.ApplyMeetingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApplyMeetingController {

    private ApplyMeetingService conferenceApplicationService;

    @PostMapping("/applyConference") //申请会议
    public ResponseEntity<Meeting> applyConference(@RequestBody MeetingRequest request) {
        return ResponseEntity.ok(conferenceApplicationService.applyConference(request));
    }

    @GetMapping("/") //申请会议
    @ResponseBody
    public String getConference() {
        return "hello world";
    }

}
