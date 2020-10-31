package com.example.demo.controller;

import com.example.demo.request.RebuttalRequest;
import com.example.demo.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RebuttalController {
    private AuthorService authorService;

    @PostMapping("/authorRebuttal") //作者rebuttal
    public ResponseEntity<String> authorRebuttal(@RequestBody RebuttalRequest rebuttalRequest) {
        return ResponseEntity.ok(authorService.authorRebuttal(rebuttalRequest));
    }
}
