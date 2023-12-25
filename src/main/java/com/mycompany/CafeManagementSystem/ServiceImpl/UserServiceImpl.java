package com.mycompany.CafeManagementSystem.ServiceImpl;

import com.google.common.base.Strings;
import com.mycompany.CafeManagementSystem.Entity.User;
import com.mycompany.CafeManagementSystem.JWT.CustomerUserDetailService;
import com.mycompany.CafeManagementSystem.JWT.JwtFilter;
import com.mycompany.CafeManagementSystem.JWT.JwtUtil;
import com.mycompany.CafeManagementSystem.Repository.UserRepository;
import com.mycompany.CafeManagementSystem.Service.IUserService;
import com.mycompany.CafeManagementSystem.Utils.CafeUtils;
import com.mycompany.CafeManagementSystem.Utils.MailUtils;
import com.mycompany.CafeManagementSystem.Wrapper.UserWrapper;
import com.mycompany.CafeManagementSystem.constants.CafeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";
    @Autowired
    UserRepository userRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomerUserDetailService customerUserDetailService;
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    JwtFilter jwtFilter;
    @Autowired
    MailUtils mailUtils;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signUp {}", requestMap);
        try {
            if (invalidateSignUpMap(requestMap)) {
                User user = userRepository.findByEmailId(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userRepository.save(getUserFormMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered!", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.USER_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public Boolean invalidateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("email")
                && requestMap.containsKey("contactNumber") && requestMap.containsKey("password")) {
            return true;
        }
        return false;
    }

    public User getUserFormMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setPassword(requestMap.get("password"));
        user.setRole("user");
        user.setStatus("false");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login {}", requestMap);
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password"))
            );
            if (auth.isAuthenticated()) {
                if (customerUserDetailService.getUserDetails().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\""
                            + jwtUtil.generateToken(customerUserDetailService.getUserDetails().getEmail()
                            , customerUserDetailService.getUserDetails().getRole()) + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"Waiting for Admin Approval\"}", HttpStatus.BAD_REQUEST);
                }
            }
        }catch (Exception e) {
            log.error("Exception: {}", e);
        }
        return new ResponseEntity<String>("{\"message\":\"Bad Credentials\"}", HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try{
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userRepository.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()){
                Optional<User> user = userRepository.findById(Long.parseLong(requestMap.get("id")));
                if (!user.isEmpty()){
                    userRepository.updateStatus(requestMap.get("status"), Long.parseLong(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"), user.get().getEmail(), userRepository.getAllAdmin());
                    return CafeUtils.getResponseEntity("User status updated successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("User id does not exists", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status !=null && status.equalsIgnoreCase("true")) {
            mailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved",
                    "USER:- " + user + " is approved by ADMIN:- "
                            + jwtFilter.getCurrentUser() + ".",allAdmin);
        } else {
            mailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled",
                    "USER:- " + user + " is disabled by ADMIN:- "
                            + jwtFilter.getCurrentUser() + ".",allAdmin);
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            if (!requestMap.get("oldPassword").isEmpty() && !requestMap.get("newPassword").isEmpty() && !requestMap.get("confirmPassword").isEmpty()) {
                User userObj = userRepository.findByEmail(jwtFilter.getCurrentUser());
                if (!userObj.equals(null)){
                    if (userObj.getPassword().equals(requestMap.get("oldPassword"))){
                        if (requestMap.get("newPassword").equals(requestMap.get("confirmPassword"))){
                            if (!requestMap.get("newPassword").equals(requestMap.get("oldPassword"))){
                                userObj.setPassword(requestMap.get("newPassword"));
                                userRepository.save(userObj);
                                return CafeUtils.getResponseEntity("Password changed successfully", HttpStatus.OK);
                            } else {
                                return CafeUtils.getResponseEntity("Old password and new password cannot be same", HttpStatus.BAD_REQUEST);
                            }
                        } else {
                            return CafeUtils.getResponseEntity("New password and confirm password does not match", HttpStatus.BAD_REQUEST);
                        }
                    } else {
                        return CafeUtils.getResponseEntity("Old password is incorrect", HttpStatus.BAD_REQUEST);
                    }
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userRepository.findByEmail(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail())) {
                String randomPassword = generateRandomPassword(8);
                mailUtils.forgotMail(user.getEmail(), "Credentials by Cafe Management System", randomPassword);
                user.setPassword(randomPassword);
                userRepository.save(user);
            }
            return CafeUtils.getResponseEntity("Check your email for Credentials.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static String generateRandomPassword(int length) {
        Random random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }
}
