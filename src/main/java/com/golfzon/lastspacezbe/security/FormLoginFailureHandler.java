package com.golfzon.lastspacezbe.security;//package com.example.seesaw.security;
//
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//public class FormLoginFailureHandler implements AuthenticationFailureHandler {
//    @Override
//    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//
//        if (exception instanceof BadCredentialsException) {
//            String errorMag1 = "can't find PW.";
//            response.getWriter().write(errorMag1);
//        }
//        if (exception instanceof UsernameNotFoundException) {
//            String errorMag2 = "can't find UserName.";
//            response.getWriter().write(errorMag2);
//        }
//    }
//}

