package com.jwt.security.Commons.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.security.SecureRandom;

@Component
public class OtpUtils {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Value("${app.otp.length}")
    private Integer length;

    @Value("${app.otp.expiration}")
    private int expiration;

    @Value("${app.otp.isTest}")
    private boolean isTestOtp;

    @Autowired
    RedisClientUtils redisClient;

    public String generateOtp(String username) throws Exception{
        String otp = generateRandInt(length);
        if(isTestOtp)
            otp = "123456";
        redisClient.setValue(username, otp, expiration);
        return otp;
    }

    public boolean verifyOtp(String username, String otp) throws Exception {
        try {
            log.info("Verifying User: {}, OTP:{}", username, otp);

            String userOtp = redisClient.getValue(username);
            log.info("Redis OTP : {}, isEqual : {}", userOtp, userOtp.equals(otp));
            return userOtp.equals(otp);

        } catch (Exception e) {
            log.error("Exception in verifyOtp: {}", e.getMessage());
            throw  new  Exception("OTP not generated/expired for given user");
        }
    }

    private static String generateRandInt(int length){
        String numbers = "0123456789";
        SecureRandom randomMethod = new SecureRandom();
        char[] otp = new char[length];
        for (int i = 0; i < length; i++)
        {
            otp[i] = numbers.charAt(randomMethod.nextInt(numbers.length()));
        }
        return new String(otp);
    }

    public void removeOtp(String username) {
        redisClient.deleteKey(username);
    }

}