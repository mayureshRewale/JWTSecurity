package com.jwt.security.AuthSecurity;

import com.jwt.security.Dao.Entities.Roles;
import com.jwt.security.Dao.Entities.UserDetails;
import com.jwt.security.Dao.Repositories.RolesRepository;
import com.jwt.security.Dao.Repositories.UserDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserDetailsRepository userDetailsRepository;

    @Autowired
    RolesRepository rolesRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDetails userDetailsEntity = userDetailsRepository.findByUsernameIgnoreCaseAndIsActive(username, true);
        List<String> roleNameList = Arrays.asList(userDetailsEntity.getRoles().split(","));
        List<Roles> rolesList = rolesRepository.findByNameIn(roleNameList);
        if (userDetailsEntity != null) {
            return new User(userDetailsEntity.getUsername(), "", getGrantedAuthorities(rolesList));
        }
        throw new UsernameNotFoundException("User not found with number " + username);
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<Roles> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Roles role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

}