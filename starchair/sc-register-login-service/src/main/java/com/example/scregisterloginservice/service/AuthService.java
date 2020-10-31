package com.example.scregisterloginservice.service;

import com.example.scregisterloginservice.controller.request.RegisterRequest;
import com.example.scregisterloginservice.domain.User;
import com.example.scregisterloginservice.exception.MailBoxHasBeenRegisteredException;
import com.example.scregisterloginservice.exception.UsernameHasBeenRegisteredException;
import com.example.scregisterloginservice.repository.UserRepository;
import com.example.scregisterloginservice.security.jwt.JwtConfigProperties;
import com.example.scregisterloginservice.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import com.example.scregisterloginservice.exception.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * @author LBW
 */
@Service
public class AuthService {
    private AuthenticationManager authenticationManager = new AuthenticationManager() {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            return null;
        }
    };
    private UserRepository userRepository;
    private JwtTokenUtil jwtTokenUtil = new JwtTokenUtil(new JwtConfigProperties());
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 实现用户注册，将新用户存入user库
     *
     * @param request 注册请求，有用户注册的UserRepository
     *                基本信息
     * @return 新注册的用户
     */
    public User register(RegisterRequest request) {
        String username = request.getUsername();
        String email = request.getE_mail();
        if (userRepository.findByUsername(username) != null) {
            throw new UsernameHasBeenRegisteredException(username); //用户名已被注册
        } else if(userRepository.findByE_mail(email) != null){
            throw new MailBoxHasBeenRegisteredException();
        }
        else {
            User newUser = new User(username, request.getFullname(),
                    encoder.encode(request.getPassword()), request.getE_mail(),
                    request.getCompanie(), request.getArea(), new HashSet<>());
            userRepository.save(newUser);
            return newUser;
        }
    }

    /**
     * 用户登录实现
     *
     * @param username 用户名
     * @param password 密码
     * @return token和用户信息
     */
    public Map<String, Object> login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) { //用户不存在
            throw new UsernameNotFoundException(username);
        } else if (!encoder.matches(password, user.getPassword())) { //密码不正确
            throw new BadCredentialsException();
        } else {
            UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(upToken);
            String token = jwtTokenUtil.generateToken(user); //生成token
            Map<String, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("userDetails", user);
            return map;
        }
    }
}

