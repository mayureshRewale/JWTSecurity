package com.jwt.security.TestService.Controller;

import com.jwt.security.Dao.Beans.ServiceResponseBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @GetMapping("/test-ping")
    public ServiceResponseBean getOtp() {
        log.info("Test Ping Successful");

        return ServiceResponseBean.builder().status(Boolean.TRUE).message("Test Ping is Successful").build();
    }

}
