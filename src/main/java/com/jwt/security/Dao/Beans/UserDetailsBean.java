package com.jwt.security.Dao.Beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDetailsBean {

    private long id;

    private String userName;

    private String password;

    private String mobile;

    private String email;

    private String userId;

    private String firstName;

    private String middleName;

    private String lastName;

    private List<String> roles;

}