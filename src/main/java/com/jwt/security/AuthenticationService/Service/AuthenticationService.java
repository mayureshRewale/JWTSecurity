package com.jwt.security.AuthenticationService.Service;

import com.jwt.security.Dao.Beans.RegisterUserBean;
import com.jwt.security.Dao.Beans.ServiceResponseBean;

public interface AuthenticationService {

    ServiceResponseBean getOtp(String contact, String otpType);

    ServiceResponseBean verifyOtp(String contact, String otp, String otpType);

    ServiceResponseBean registerUser(RegisterUserBean registerUserDTO);

    ServiceResponseBean setMpin(String contact, String mpin);

}
