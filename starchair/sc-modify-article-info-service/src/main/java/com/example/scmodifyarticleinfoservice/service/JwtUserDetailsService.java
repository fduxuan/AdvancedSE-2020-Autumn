package com.example.scmodifyarticleinfoservice.service;

import com.example.scmodifyarticleinfoservice.domain.User;
import com.example.scmodifyarticleinfoservice.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author LBW
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User: '" + username + "' not found.");
        }
        return user;
    }
}
