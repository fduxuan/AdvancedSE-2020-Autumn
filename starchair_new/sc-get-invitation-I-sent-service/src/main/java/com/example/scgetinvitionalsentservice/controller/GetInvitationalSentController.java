package com.example.scgetinvitionalsentservice.controller;


import com.example.scgetinvitionalsentservice.service.UserCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class GetInvitationalSentController {
    private UserCenterService userCenterService;

    @Autowired
    public GetInvitationalSentController(UserCenterService userCenterService) {
        this.userCenterService = userCenterService;
    }


    @GetMapping("/invitationISent") //获得我发出的邀请
    public ResponseEntity<Map<String, Object>> getInvitationISent(@RequestParam("username") String username) {
        return ResponseEntity.ok(userCenterService.invitationISent(username));
    }
}
