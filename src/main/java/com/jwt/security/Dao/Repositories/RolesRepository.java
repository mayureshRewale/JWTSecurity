package com.jwt.security.Dao.Repositories;

import com.jwt.security.Dao.Entities.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {

    Roles findByNameAndIsActive(String string, boolean isActive);

    List<Roles> findByNameIn(List<String> roleNameList);
}