package com.example.scexaminemanuscripts.controller;

import com.example.scexaminemanuscripts.controller.request.ModifyScoreRequest;
import com.example.scexaminemanuscripts.controller.request.ScoreRequest;
import com.example.scexaminemanuscripts.service.PcMemberService;
import com.example.scexaminemanuscripts.service.UserCenterService;
import org.apache.commons.io.FileUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ExamineManusciptsController {
    private PcMemberService pcMemberService;

    private UserCenterService userCenterService;

    @GetMapping("/userConf") //我参与的所有会议
    public ResponseEntity<List<Object>> getMyMeeting(@RequestParam("username") String username) {
        return ResponseEntity.ok(userCenterService.myMeeting(username));
    }

    @RequestMapping(value = "/showPdf", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_PCMEMBER')")
    public ResponseEntity<byte[]> pdfDownload(HttpServletRequest httpServletRequest) throws IOException {
        String path = httpServletRequest.getParameter("path");
        File file = new File(path);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.parseMediaType("application/pdf"));
        return new ResponseEntity<>(FileUtils.readFileToByteArray(file),
                httpHeaders,
                HttpStatus.OK);
    }
    
    @GetMapping("/getAllotedArticle") //查看分配到的稿件信息
    @PreAuthorize("hasRole('ROLE_PCMEMBER')")
    public ResponseEntity<Map<String,Object>> getAllotedArticle(@RequestParam("username") String username,
                                                                @RequestParam("meetingId") String meetingId) {
        return ResponseEntity.ok(pcMemberService.getAllotedArticle(username, meetingId));
    }

    @PostMapping("/submitScore") //提交评分
    @PreAuthorize("hasRole('ROLE_PCMEMBER')")
    public ResponseEntity<String> submitScore(@RequestBody ScoreRequest scoreRequest) {
        return ResponseEntity.ok(pcMemberService.submitScore(scoreRequest));
    }
}
