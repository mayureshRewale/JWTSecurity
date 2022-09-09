package com.jwt.security.Dao.Beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestBean {

    private String mobile;
    private String otp;
    private String mpin;
    private String username;
    private String password;

}