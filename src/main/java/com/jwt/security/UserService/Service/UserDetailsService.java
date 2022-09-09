package com.jwt.security.UserService.Service;

import com.jwt.security.Dao.Beans.ServiceResponseBean;

public interface UserDetailsService {

    ServiceResponseBean getAllUsers();

    ServiceResponseBean getUserByMobile(String mobile);

}
