package com.golfzon.lastspacezbe.security;

import com.golfzon.lastspacezbe.security.jwt.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    //    public static final String AUTH_HEADER = "Authorization";
    //public static final String REFRESH_TOKEN = "refreshToken";
    public static final String ACCESS_TOKEN = "Authorization";//"accessToken";
    public static final String TOKEN_TYPE = "Bearer";

    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {
        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());
        // AccessToken 생성
        final String accesstoken = JwtTokenUtils.generateJwtToken(userDetails.getMember());
        System.out.println(userDetails.getUsername() + "'s token : " + TOKEN_TYPE + " " + accesstoken);
        response.addHeader(ACCESS_TOKEN, TOKEN_TYPE + " " + accesstoken);

        System.out.println("LOGIN SUCCESS!");

    }
}