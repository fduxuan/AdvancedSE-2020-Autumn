package com.example.scstartsubmitservice.controller;


import com.example.scstartsubmitservice.service.ChairService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
public class StartSubmitController {
    private ChairService chairService;

    @GetMapping("/changeSubmitStatus") //开启投稿
    @PreAuthorize("hasRole('ROLE_CHAIR')")
    public ResponseEntity<Map<String, String>> changeSubmitStatus(@RequestParam("meetingID") String meetingID,
                                                                  @RequestParam("submitStatus") String submitStatus) {
        Map<String, String> response = new HashMap<>();
        response.put("message", chairService.changeSubmitStatus(meetingID, submitStatus));
        return ResponseEntity.ok(response);
    }
}
