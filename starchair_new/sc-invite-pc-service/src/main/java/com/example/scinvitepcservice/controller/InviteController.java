package com.example.scinvitepcservice.controller;

import com.example.scinvitepcservice.controller.request.SendInvitationRequest;
import com.example.scinvitepcservice.domain.User;
import com.example.scinvitepcservice.service.ChairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class InviteController {
    private ChairService chairService;

    @Autowired
    public InviteController(ChairService chairService) {
        this.chairService = chairService;
    }

    @GetMapping("/searchUserInfo") //通过查找用户的真实姓名返回user list
    @PreAuthorize("hasRole('ROLE_CHAIR')")
    public ResponseEntity<List<User>> getSearchForUser(@RequestParam("fullName") String fullName) {
        return ResponseEntity.ok(chairService.searchForUser(fullName));
    }

    @PostMapping("/sendInvitation") //发送邀请到所有被邀请用户
    @PreAuthorize("hasRole('ROLE_CHAIR')")
    public ResponseEntity<Map<String, String>> sendInvitation(@RequestBody SendInvitationRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put("message", chairService.sendInvitation(request.getMeetingID(),
                request.getInviter(), request.getInvitee()));
        return ResponseEntity.ok(response);
    }

}
