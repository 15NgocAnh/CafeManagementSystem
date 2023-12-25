package com.mycompany.CafeManagementSystem.JWT;

import com.mycompany.CafeManagementSystem.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Slf4j
@Service
public class CustomerUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    private com.mycompany.CafeManagementSystem.Entity.User userDetails;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Inside loadUserByUsername {}", username);
        userDetails = userRepository.findByEmailId(username);
        if(!Objects.isNull(userDetails)){
            return new User(userDetails.getEmail(),userDetails.getPassword(),new ArrayList<>());
        }
        else{
            throw new UsernameNotFoundException("User not found");
        }
    }

    public com.mycompany.CafeManagementSystem.Entity.User getUserDetails(){
        return userDetails;
    }
}
