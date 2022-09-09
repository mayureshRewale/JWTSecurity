package com.jwt.security.AuthenticationService.ServiceImpl;

import com.jwt.security.AuthenticationService.Service.AuthenticationService;
import com.jwt.security.Commons.Utils.OtpUtils;
import com.jwt.security.Dao.Beans.RegisterUserBean;
import com.jwt.security.Dao.Beans.ServiceResponseBean;
import com.jwt.security.Dao.Beans.UserDetailsBean;
import com.jwt.security.Dao.Entities.Roles;
import com.jwt.security.Dao.Entities.UserDetails;
import com.jwt.security.Dao.Repositories.RolesRepository;
import com.jwt.security.Dao.Repositories.UserDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    OtpUtils otpUtils;

    @Autowired
    RolesRepository roleRepository;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Autowired
    UserDetailsRepository userRepository;

    @Override
    public ServiceResponseBean getOtp(String contact, String otpType) {
        log.info("Request in getOtp");

        String otp = "";

        try {

            UserDetails userDetailsEntity = userDetailsRepository.findByMobile(contact);

            if(otpType.isBlank()) {
                return ServiceResponseBean.builder().errorMessage("Invalid OTP Type").status(Boolean.FALSE).build();
            }

            switch (otpType) {
                case "LOGIN":

                    if(Objects.isNull(userDetailsEntity)) {
                        return ServiceResponseBean.builder().errorMessage("No User Found with mobile no. " + contact).status(Boolean.FALSE).build();
                    }
                    otp = otpUtils.generateOtp(contact+"_LOGIN");
                    log.info("OTP Value : {}", otp);

                    break;

                case "SIGNUP":
                    if(Objects.nonNull(userDetailsEntity)) {
                        return ServiceResponseBean.builder().errorMessage("User with mobile no. " + contact + " already exists").status(Boolean.FALSE).build();
                    }
                    otp = otpUtils.generateOtp(contact+"_SIGNUP");
                    log.info("OTP Value : {}", otp);

                    break;

                default:
                    return ServiceResponseBean.builder().errorMessage("Invalid OTP Type").status(Boolean.FALSE).build();

            }

        }catch (Exception e) {
            log.error("Exception : {}", e);
            return ServiceResponseBean.builder().errorMessage("Something went wrong.. Please try again later").status(Boolean.FALSE).build();
        }
        return ServiceResponseBean.builder().message("OTP for " + contact +" generated successfully").data(otp).status(Boolean.TRUE).build();
    }

    @Override
    public ServiceResponseBean verifyOtp(String contact, String otp, String otpType) {
        log.info("Request in verifyOtp");

        Boolean isOtpVerified = Boolean.FALSE;

        try {

            if(otpType.isBlank()) {
                return ServiceResponseBean.builder().errorMessage("Invalid OTP Type").status(Boolean.FALSE).build();
            }

            switch (otpType) {
                case "LOGIN":
                    isOtpVerified = otpUtils.verifyOtp(contact+"_LOGIN", otp);
                    log.info("OTP Verified : {}", isOtpVerified);

                    break;

                case "SIGNUP":
                    isOtpVerified = otpUtils.verifyOtp(contact+"_SIGNUP", otp);
                    log.info("OTP Verified : {}", isOtpVerified);

                    break;

                default:
                    break;
            }

        }catch (Exception e) {
            log.error("Exception : {}", e);
            return ServiceResponseBean.builder().errorMessage("Something went wrong.. Please try again later").status(Boolean.FALSE).build();
        }
        return isOtpVerified ? ServiceResponseBean.builder().message("OTP for " + contact +" Verified successfully").data(otp).status(Boolean.TRUE).build() :
                ServiceResponseBean.builder().message("Incorrect OTP").status(Boolean.FALSE).build();
    }

    @Override
    public ServiceResponseBean registerUser(RegisterUserBean registerUserDTO) {
        log.info("Request in registerUser");

        UserDetails userDetailsEntity = new UserDetails();

        try {

            userDetailsEntity.setEmail(registerUserDTO.getEmail());
            userDetailsEntity.setMobile(registerUserDTO.getMobile());
            userDetailsEntity.setUsername(registerUserDTO.getMobile());

            Roles userRole = roleRepository.findByNameAndIsActive("ROLE_USER", true);
            if (userRole != null) {
                log.info("Setting Roles");
                userDetailsEntity.setRoles(userRole.getName());
            }

            userDetailsEntity.setFirstName(registerUserDTO.getFirstName());
            userDetailsEntity.setMiddleName(registerUserDTO.getMiddleName());
            userDetailsEntity.setLastName(registerUserDTO.getLastName());
            userDetailsEntity.setCreatedDate(LocalDateTime.now());

            UserDetails savedUser = userDetailsRepository.save(userDetailsEntity);
            log.info("UserDetails :: {}", savedUser);

            return Objects.nonNull(savedUser) ? ServiceResponseBean.builder().message("User Registered Successfully").data(savedUser).status(Boolean.TRUE).build() :
                    ServiceResponseBean.builder().message("Incorrect OTP").status(Boolean.FALSE).build();
        }catch (Exception e) {
            log.error("Exception : {}", e);
            return ServiceResponseBean.builder().errorMessage("Something went wrong.. Please try again later").status(Boolean.FALSE).build();
        }
    }

    public void setUserRole(UserDetails savedUser) {
        Roles userRole = roleRepository.findByNameAndIsActive("ROLE_USER", true);
        if (userRole != null) {
            savedUser.setRoles(userRole.getName());
            userDetailsRepository.save(savedUser);
        }
    }

    public void setAdminRole(UserDetails savedUser) {
        Roles userRole = roleRepository.findByNameAndIsActive("ROLE_ADMIN", true);
        if (userRole != null) {
            savedUser.setRoles(userRole.getName());
            userDetailsRepository.save(savedUser);
        }
    }

    public void setSuperAdminRole(UserDetails savedUser) {
        List<String> rolesList = roleRepository.findAll().stream().map(roles -> roles.getName()).collect(Collectors.toList());
        if (Objects.nonNull(rolesList) && rolesList.size() > 0) {
            log.info("Setting Roles");
            savedUser.setRoles(String.join(",", rolesList));
        }
    }

    @Override
    public ServiceResponseBean setMpin(String contact, String mpin) {
        log.info("Request in setMpin");

        if(mpin.isBlank())
            return ServiceResponseBean.builder().errorMessage("Mpin cannot be empty").status(Boolean.FALSE).build();

        try {

            UserDetails userDetailsEntity = userDetailsRepository.findByMobile(contact);
            log.info("AuthenticationDAO : {}", userDetailsEntity);

            if(Objects.nonNull(userDetailsEntity))
                userDetailsEntity.setMpin(mpin);
            else
                return ServiceResponseBean.builder().errorMessage("No User found with mobile no." + contact).status(Boolean.FALSE).build();

            UserDetails savedUserDetails = userDetailsRepository.save(userDetailsEntity);

            return Objects.nonNull(savedUserDetails) ? ServiceResponseBean.builder().message("Mpin Set Successfully").status(Boolean.TRUE).build() :
                    ServiceResponseBean.builder().message("Something went wrong.. Please try again later").status(Boolean.FALSE).build();
        }catch (Exception e) {
            log.error("Exception : {}", e);
            return ServiceResponseBean.builder().errorMessage("Something went wrong.. Please try again later").status(Boolean.FALSE).build();
        }
    }

}