package com.golfzon.lastspacezbe.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.golfzon.lastspacezbe.member.entity.Member;

import java.util.Date;

public final class JwtTokenUtils {

    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    // JWT 토큰의 유효기간: 3일 (단위: seconds)
    private static final int JWT_TOKEN_VALID_SEC = 3 * DAY;
    // JWT 토큰의 유효기간: 3일 (단위: milliseconds)
    private static final int JWT_TOKEN_VALID_MILLI_SEC = JWT_TOKEN_VALID_SEC * 1000;

    public static final String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";
    public static final String CLAIM_EMAIL = "USER_EMAIL";
    public static final String UID = "UID";
    public static final String NICKNAME = "NICKNAME";
    public static final String IMAGE = "IMAGE";
    public static final String AUTHORITY = "AUTHORITY";
    public static final String JWT_SECRET = "jwt_secret_!@#$%";

    public static String generateJwtToken(Member member) {
        String accessToken = null;

        try {
            accessToken = JWT.create()
                    .withIssuer("spacez")
                    .withClaim(CLAIM_EMAIL, member.getEmail())
                     // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                    .withClaim(UID, member.getMemberId())
                    .withClaim(NICKNAME, member.getMemberName())
                    .withClaim(IMAGE, member.getImgName())
                    .withClaim(AUTHORITY, member.getAuthority())
                    .sign(generateAlgorithm());
            System.out.println("accessToken 생성 : "+ accessToken);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return accessToken;
    }


    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(JWT_SECRET);
    }
}
