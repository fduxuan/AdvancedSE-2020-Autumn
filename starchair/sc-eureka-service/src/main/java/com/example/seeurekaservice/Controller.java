package com.example.seeurekaservice;


import com.example.seeurekaservice.domain.Meeting;
import com.example.seeurekaservice.domain.User;
import com.example.seeurekaservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class Controller {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/123123") //申请会议
    @ResponseBody
    public String getConference() {
        User user = userRepository.findByE_mail("1@qq.com");
        if(user==null)
            System.out.println(user.toString());
        return user.toString();
    }

}
