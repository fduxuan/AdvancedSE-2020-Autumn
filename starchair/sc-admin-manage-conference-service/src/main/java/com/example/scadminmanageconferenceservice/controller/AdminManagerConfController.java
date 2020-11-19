package com.example.scadminmanageconferenceservice.controller;

import com.example.scadminmanageconferenceservice.controller.request.ChangeApplicationStatusRequest;
import com.example.scadminmanageconferenceservice.domain.Meeting;
import com.example.scadminmanageconferenceservice.service.ConferenceApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminManagerConfController {
    private ConferenceApplicationService conferenceApplicationService;

    @GetMapping("/getUncheckedConference") //管理员页面返回待审核会议
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Object>> getUncheckedConference() {
        return ResponseEntity.ok(conferenceApplicationService.getUncheckedConference());
    }

    @PostMapping("/changeApplicationStatus") //管理员审核会议后改变会议申请状态
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<Meeting>> changeApplicationStatus(@RequestBody ChangeApplicationStatusRequest request) {
        return ResponseEntity.ok(conferenceApplicationService.changeApplicationStatus(request));
    }

}
