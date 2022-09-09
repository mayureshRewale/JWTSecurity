package com.jwt.security.Dao.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "user_details")
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "is_active")
    private Boolean isActive=true;

    @Column(name = "qud_first_name")
    private String firstName;

    @Column(name = "qud_middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username", unique=true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "mobile", unique=true)
    private String mobile;

    @Column(name = "otp_verification")
    private Boolean otpVerification = true;

    @Column(name = "email")
    private String email;

    @Column(name = "last_login")
    private LocalDateTime lastlogin;

    @Column(name = "mpin")
    private String mpin;

    @Column(name = "roles")
    private String roles;

}