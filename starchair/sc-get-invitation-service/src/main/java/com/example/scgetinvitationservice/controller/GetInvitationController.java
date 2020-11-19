package com.example.scgetinvitationservice.controller;

import com.example.scgetinvitationservice.controller.request.ChangeInvitationRequest;
import com.example.scgetinvitationservice.service.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class GetInvitationController {
    private UserCenterService userCenterService;

    @GetMapping("/invitationIReceived") //获得我收到的所有邀请
    public ResponseEntity<Map<String, Object>> getInvitationIReceived(@RequestParam("id") String id) {
        return ResponseEntity.ok(userCenterService.invitationIReceived(id));
    }


    @PostMapping("/changeInvitationStatus") //我通过或拒绝邀请后改变邀请状态
    public ResponseEntity<Map<String, Object>> changeInvitationStatus(@RequestBody ChangeInvitationRequest request) {
        return ResponseEntity.ok(userCenterService.changeInvitationStatus(request.getId(),
                request.getInvitationStatus(), request.getTopics()));
    }
}
