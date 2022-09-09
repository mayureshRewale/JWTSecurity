package com.jwt.security.Dao.Repositories;

import com.jwt.security.Dao.Entities.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {

    UserDetails findByUsernameIgnoreCaseAndIsActive(String username, boolean isActive);

    UserDetails findByMobile(String contact);

    UserDetails findByUsernameIgnoreCase(String username);

}