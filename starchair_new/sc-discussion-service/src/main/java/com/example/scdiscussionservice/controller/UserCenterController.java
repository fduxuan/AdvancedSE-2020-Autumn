package com.example.scdiscussionservice.controller;

import com.example.scdiscussionservice.domain.Authority;
import com.example.scdiscussionservice.service.UserCenterService;
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


    @GetMapping("/userConf") //我参与的所有会议
    public ResponseEntity<List<Object>> getMyMeeting(@RequestParam("username") String username) {
        return ResponseEntity.ok(userCenterService.myMeeting(username));
    }

    @GetMapping("/getAuthorityList") //获取该用户在指定会议的角色列表
    public ResponseEntity<List<Authority>> getAuthorityList(@RequestParam("id") String id,
                                                            @RequestParam("username") String username) {
        return ResponseEntity.ok(userCenterService.getUser_MeetingAuthorityList(id, username));
    }

}
