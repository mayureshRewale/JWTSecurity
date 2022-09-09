package com.jwt.security.UserService.Controller;

import com.jwt.security.Dao.Beans.ServiceResponseBean;
import com.jwt.security.UserService.Service.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-details")
public class UserDetailsController {

    @Autowired
    UserDetailsService userDetailsService;

    @GetMapping("/get-all-users")
    public ServiceResponseBean getAllUsers() {
        return userDetailsService.getAllUsers();
    }

    @GetMapping("/get-user-by-mobile")
    public ServiceResponseBean getUserByMobile(@RequestParam("mobile") String mobile) {
        return userDetailsService.getUserByMobile(mobile);
    }

}
