package com.jwt.security.UserService.ServiceImpl;

import com.jwt.security.Dao.Beans.ServiceResponseBean;
import com.jwt.security.Dao.Entities.UserDetails;
import com.jwt.security.Dao.Repositories.UserDetailsRepository;
import com.jwt.security.UserService.Service.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserDetailsRepository userDetailsRepository;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public ServiceResponseBean getAllUsers() {
        log.info("Request received in getAllUsers");

        List<UserDetails> userDetailsList = userDetailsRepository.findAll();
        log.info("UserDetails List :: {}", userDetailsList);

        return Objects.nonNull(userDetailsList) && userDetailsList.size() > 0 ?
                ServiceResponseBean.builder().status(Boolean.TRUE).data(userDetailsList).build() :
                ServiceResponseBean.builder().status(Boolean.FALSE).errorMessage("Users not found").build();
    }

    @Override
    public ServiceResponseBean getUserByMobile(String mobile) {log.info("Request received in getAllUsers");
        log.info("Request received in getAllUsers");

        UserDetails userDetails = userDetailsRepository.findByMobile(mobile);
        log.info("UserDetails :: {}", userDetails);
        return Objects.nonNull(userDetails) ?
                ServiceResponseBean.builder().status(Boolean.TRUE).data(userDetails).build() :
                ServiceResponseBean.builder().status(Boolean.FALSE).errorMessage("User not found").build();
    }
}
