package com.jwt.security.Config;

import com.jwt.security.Dao.Beans.UserDetailsBean;
import com.jwt.security.Dao.Entities.Roles;
import com.jwt.security.Dao.Entities.UserDetails;
import com.jwt.security.Dao.Repositories.RolesRepository;
import com.jwt.security.Dao.Repositories.UserDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ApplicationSetupConfig implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Value("${default.admin.username}")
    private String adminUsername;

    @Value("${default.admin.password}")
    private String adminPassword;

    @Value("${default.admin.email}")
    private String adminEmail;

    @Value("${default.admin.first.name}")
    private String adminFirstName;

    @Value("${default.admin.middle.name}")
    private String adminMiddleName;

    @Value("${default.admin.last.name}")
    private String adminLastName;

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        addRolesIfNotExists(Arrays.asList("ROLE_ADMIN","ROLE_USER"));
        addAdminUserIfNotExists();

    }

    @Transactional
    private void addAdminUserIfNotExists() {

        try{
            UserDetails userDetails = userDetailsRepository.findByUsernameIgnoreCase(adminUsername);

            if(userDetails == null) {
                UserDetails adminUserDetails = new UserDetails();
                UserDetails daoUser = new UserDetails();

                adminUserDetails.setEmail(adminEmail);
                adminUserDetails.setUsername(adminUsername);
                adminUserDetails.setPassword(adminPassword);

                List<String> rolesString = rolesRepository.findAll().stream().map(roles -> roles.getName()).collect(Collectors.toList());
                if (Objects.nonNull(rolesString) && rolesString.size() > 0) {
                    log.info("Setting Roles");
                    adminUserDetails.setRoles(String.join(",", rolesString));
                }

                daoUser.setFirstName(adminFirstName);
                daoUser.setMiddleName(adminMiddleName);
                daoUser.setLastName(adminLastName);

                UserDetails savedUser = userDetailsRepository.save(adminUserDetails);
            }else {
                log.info("Admin User Exists");
            }

        }catch (Exception e){
            log.info("Exception on setting up ADMIN User");
        }

    }

    @Transactional
    private void addRolesIfNotExists(List<String> rolesList) {
        List<Roles> rolesEntityList = new ArrayList<>();
        for(String role : rolesList){
            log.info("Role :: {}", role);
            if(Objects.isNull(rolesRepository.findByNameAndIsActive(role, Boolean.TRUE))){
                Roles rolesEntity = new Roles();
                rolesEntity.setName(role);
                rolesEntity.setDescription(role.replace("ROLE_", ""));
                rolesEntityList.add(rolesEntity);
            }
        }
        if(Objects.nonNull(rolesEntityList) && rolesEntityList.size() > 0){
            rolesRepository.saveAll(rolesEntityList);
        }

    }
}