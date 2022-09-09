package com.jwt.security.AuthenticationService.Controller;

import com.jwt.security.AuthenticationService.Service.AuthenticationService;
import com.jwt.security.Dao.Beans.RegisterUserBean;
import com.jwt.security.Dao.Beans.ServiceResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @Autowired
    AuthenticationService authService;

    /* Registration Flow
     * 1. get-otp		-> generate OTP for contact
     * 2. verify-otp 	-> verify OTP for contact
     * 3. register 		-> register user details
     * 4. set-mpin 		-> set mpin for contact
     * 5. authenticate 	-> Login with mobile and mpin
     */

    @GetMapping("/get-otp")
    public ServiceResponseBean getOtp(@RequestParam("contact") String contact, @RequestParam("otp_type") String otpType) {
        ServiceResponseBean serviceResponse = authService.getOtp(contact, otpType);
        return serviceResponse;
    }

    @GetMapping("/verify-otp")
    public ServiceResponseBean verifyOtp(@RequestParam("contact") String contact, @RequestParam("otp") String otp, @RequestParam("otp_type") String otpType) {
        ServiceResponseBean serviceResponse = authService.verifyOtp(contact, otp, otpType);
        return serviceResponse;
    }

    @PostMapping("/register")
    public ServiceResponseBean registerUser(@RequestBody RegisterUserBean registerUserDTO) {
        ServiceResponseBean serviceResponse = authService.registerUser(registerUserDTO);
        return serviceResponse;
    }

    @PutMapping("/set-mpin")
    public ServiceResponseBean setMpin(@RequestParam("contact") String contact, @RequestParam("mpin") String mpin) {
        ServiceResponseBean serviceResponse = authService.setMpin(contact, mpin);
        return serviceResponse;
    }

}