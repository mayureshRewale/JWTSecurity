package com.jwt.security.AuthSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.security.Commons.Utils.CommonUtils;
import com.jwt.security.Commons.Utils.JwtUtil;
import com.jwt.security.Commons.Utils.OtpUtils;
import com.jwt.security.Dao.Beans.LoginRequestBean;
import com.jwt.security.Dao.Entities.UserDetails;
import com.jwt.security.Dao.Repositories.UserDetailsRepository;
import com.jwt.security.SpringApplicationContext;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private Boolean isTestMode = Boolean.TRUE;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final UserDetailsRepository userDetailsRepository = (UserDetailsRepository) SpringApplicationContext.getBean("userDetailsRepository");

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, RuntimeException {
        try {
            LoginRequestBean creds = new ObjectMapper().readValue(req.getInputStream(), LoginRequestBean.class);
            String username;
            if (creds.getUsername() != null && creds.getUsername() != ""){
                username = creds.getUsername();
                if(!verifyADUser(username, creds.getPassword()))
                    throw new Exception("Admin User Not Authenticated");
            }
            else {
                username = creds.getMobile();
                log.info("Mobile {}, OTP {}", creds.getMobile(), creds.getOtp());

                if (!creds.getMobile().isBlank() && !creds.getOtp().isBlank() && !verifyUserByOTP(username, creds.getOtp()))
                    throw new Exception("Incorrect OTP");
                else if(!creds.getMobile().isBlank() && !creds.getMpin().isBlank() && !verifyMobileAndMpin(username, creds.getMpin()))
                    throw new Exception("Incorrect MPIN");
            }

            CustomUserDetailsService userDetailsService  = (CustomUserDetailsService) SpringApplicationContext.getBean("customUserDetailsService");
            User user = userDetailsService.loadUserByUsername(username);

            return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        } catch (Exception e) {
            log.info("Exception while authenticating user:{}", e.getMessage());
            res.setStatus(HttpStatus.OK.value());
            res.setHeader("Content-Type","application/json");
            ResponseEntity<String> resp = ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(e.getMessage());
            OutputStream out = res.getOutputStream();
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(out, resp);
            out.flush();
            return null;
        }
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {
        String userName = ((User) auth.getPrincipal()).getUsername();
        JwtUtil jwtTokenUtil = (JwtUtil) SpringApplicationContext.getBean("jwtUtil");
        log.info("Getting user with username");

        UserDetails userDetailsEntity = userDetailsRepository.findByUsernameIgnoreCaseAndIsActive(userName, true);
        log.info("Successfully fetched AuthObject");

        String token = jwtTokenUtil.getFullAutherizationToken(userName);
        res.addHeader("Authorization", token);
        log.info("Successfully Fetched Token");

        Map<String, String> userDetails = new HashMap<>();
        String fullName = CommonUtils.getFullName(userDetailsEntity.getFirstName(), userDetailsEntity.getMiddleName(), userDetailsEntity.getLastName());
        userDetails.put("name",fullName);
        userDetails.put("username",userDetailsEntity.getUsername());
        userDetails.put("mobileNo", userDetailsEntity.getMobile());
        userDetails.put("emailId", userDetailsEntity.getEmail());
        userDetails.put("accessToken",token.substring(0, token.indexOf("|")));
        userDetails.put("refreshToken",token.substring(token.indexOf("|")+1, token.length()));

        OutputStream out = res.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, userDetails);
        log.info("Sending Final data");

        out.flush();
    }


    private boolean verifyUserByOTP(String username, String otp){
        OtpUtils otpUtils  = (OtpUtils) SpringApplicationContext.getBean("otpUtils");
        try{
            if (otpUtils.verifyOtp(username+"_LOGIN", otp)) {
                log.info("OTP Verisied - Mobile :: {}, OTP :: {}", username, otp);
                otpUtils.removeOtp(username+"_LOGIN");
                return true;
            }
        } catch (Exception e){
            log.error("Error while verifying OTP: {}",e.getMessage());
        }
        return false;
    }

    private boolean verifyADUser(String username, String password){
        if(isTestMode)
            return !CommonUtils.isNullOrEmpty(username) && !CommonUtils.isNullOrEmpty(password) && password.equals("Test@1234");
        UserDetails userDetailsEntity = userDetailsRepository.findByUsernameIgnoreCase(username);
        return !CommonUtils.isNullOrEmpty(username) && !CommonUtils.isNullOrEmpty(password) && password.equals(userDetailsEntity.getPassword());
    }

    private boolean verifyMobileAndMpin(String mobile, String mpin){
        UserDetails userDetailsEntity = userDetailsRepository.findByMobile(mobile);
        return !CommonUtils.isNullOrEmpty(mobile) && !CommonUtils.isNullOrEmpty(mpin) && mpin.equals(userDetailsEntity.getMpin());
    }

}