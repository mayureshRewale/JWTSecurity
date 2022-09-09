package com.jwt.security.AuthSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.security.Commons.Constants.CommonConstants;
import com.jwt.security.Commons.Exceptions.IdleTimeoutException;
import com.jwt.security.Commons.Utils.JwtUtil;
import com.jwt.security.SpringApplicationContext;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;

public class CustomJwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public CustomJwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            String header = req.getHeader(CommonConstants.INSTANCE.AUTHORIZATION_STRING);

            if (header == null || !header.startsWith(CommonConstants.INSTANCE.BEARER_STRING)) {
                chain.doFilter(req, res);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(req, res);
        } catch (IdleTimeoutException ie){
            log.info("Exception while authenticating user: {}", ie.getMessage());
            forbiddenResponse(601, ie.getMessage(), res);
        }
        catch (ExpiredJwtException ex){
            log.info("Exception while authenticating user: {}", ex.getMessage());
            forbiddenResponse(602, ex.getMessage(), res);
        }
        catch (Exception e) {
            log.info("Exception while authenticating user: {}", e.getMessage());
            forbiddenResponse(603, e.getMessage(), res);
        }
    }

    private void forbiddenResponse(Integer statusCode, String message, HttpServletResponse res) throws IOException{
        res.setHeader("Content-Type","application/json");
        res.setStatus(HttpStatus.OK.value());
        ResponseEntity<String> resp = ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body("commence : "+message);
        OutputStream out = res.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, resp);
        out.flush();
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws Exception {
        String header = request.getHeader(CommonConstants.INSTANCE.AUTHORIZATION_STRING);

        if (header != null) {
            String token = header.replace(CommonConstants.INSTANCE.BEARER_STRING, CommonConstants.INSTANCE.BLANK_STRING);
            JwtUtil jwtTokenUtil  = (JwtUtil) SpringApplicationContext.getBean("jwtUtil");
            if (!jwtTokenUtil.validateToken(token)){
                log.info("Returning null");
                return null;
            }
            log.info("Getting User");
            String user = jwtTokenUtil.getUsernameFromToken(token);
            log.info("Getting User :: {}", user);
            if (user != null) {
                CustomUserDetailsService userDetailsService  = (CustomUserDetailsService) SpringApplicationContext
                        .getBean("customUserDetailsService");
                User userDetails = userDetailsService.loadUserByUsername(user);
                log.info("User :: {}", userDetails);
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            }
            return null;
        }
        return null;
    }

}