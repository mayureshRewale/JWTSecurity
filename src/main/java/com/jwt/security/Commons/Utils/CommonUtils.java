package com.jwt.security.Commons.Utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static long getCurrentTimeSec(){
        return getCurrentTimeMillis() / 1000;
    }

    public static long getCurrentTimeMillis(){
        return System.currentTimeMillis();
    }

    public static String getDateTime(long milliseconds, String format){
        Date date = new Date(milliseconds);
        DateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    public static String generateRandomString(int length) {
        SecureRandom RANDOM = new SecureRandom();
        String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder returnValue = new StringBuilder();

        for (int i=0; i<length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(returnValue);
    }

    public static String getFullName(String firstName, String midName, String lastName) {
        String fullName = "";
        if (!isNullOrEmpty(firstName)) {
            fullName = fullName+firstName;
        }
        if (!isNullOrEmpty(midName)) {
            fullName = fullName+" "+midName;
        }
        if (!isNullOrEmpty(lastName)) {
            fullName = fullName+" "+lastName;
        }
        return fullName;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String generateRandInt(int length)
    {
        String numbers = "0123456789";
        SecureRandom randomMethod = new SecureRandom();
        char[] otp = new char[length];
        for (int i = 0; i < length; i++)
        {
            otp[i] = numbers.charAt(randomMethod.nextInt(numbers.length()));
        }
        return new String(otp);
    }

}