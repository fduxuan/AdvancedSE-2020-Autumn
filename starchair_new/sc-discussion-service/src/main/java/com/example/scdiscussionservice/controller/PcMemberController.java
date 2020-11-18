package com.example.scdiscussionservice.controller;

import com.example.scdiscussionservice.controller.request.ModifyScoreRequest;
import com.example.scdiscussionservice.controller.request.ScoreRequest;
import com.example.scdiscussionservice.service.PcMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PcMemberController {
    private PcMemberService pcMemberService;

    @Autowired
    public PcMemberController(PcMemberService pcMemberService) {
        this.pcMemberService = pcMemberService;
    }


    @PostMapping("/submitScore") //查看分配到的稿件信息
    @PreAuthorize("hasRole('ROLE_PCMEMBER')")
    public ResponseEntity<String> submitScore(@RequestBody ScoreRequest scoreRequest) {
        return ResponseEntity.ok(pcMemberService.submitScore(scoreRequest));
    }

    @PostMapping("/submitModify") //修改评分
    @PreAuthorize("hasRole('ROLE_PCMEMBER')")
    public ResponseEntity<String> submitModify(@RequestBody ModifyScoreRequest modifyScoreRequest) {
        return ResponseEntity.ok(pcMemberService.submitModify(modifyScoreRequest));
    }

}
