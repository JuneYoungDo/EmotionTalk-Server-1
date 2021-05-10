package com.spring.emotionTalk.utils;


import com.spring.emotionTalk.config.BaseException;
import com.spring.emotionTalk.config.secret.Secret;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import static com.spring.emotionTalk.config.BaseResponseStatus.*;

@Service
public class JwtService {

    /*
    JWT 생성
    @param userKey
    @return String
     */
    public String createJwt(int userKey){
        Date now = new Date();
        Long expiredTime = 1000 * 60L * 60L * 24L ; // 유효기간 24시간

        Date nowExpiredTime = new Date();       // 유효기간 끝나는 시각
        nowExpiredTime.setTime(nowExpiredTime.getTime() + expiredTime);

        return Jwts.builder()
                .claim("userKey",userKey)
                .setIssuedAt(now)
                .setExpiration(nowExpiredTime)
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
                .compact();
    }

    /*
    refreshToken 생성
    @param userKey
    @return String
     */
    public String createRefreshToken(int userKey){
        Date reNow = new Date();
        Long expiredTime = 1000 * 60L * 60L * 24L * 60L ; // 유효기간 60일

        Date nowExpiredTime = new Date();       // 유효기간 끝나는 시각
        nowExpiredTime.setTime(nowExpiredTime.getTime() + expiredTime);

        return Jwts.builder()
                .claim("userKey",userKey)
                .setIssuedAt(reNow)
                .setExpiration(nowExpiredTime)
                .signWith(SignatureAlgorithm.HS256, Secret.REFRESH_SECRET_KEY)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }


    /*
   JWT 검증(refreshToken)
    */
    public Boolean verifyJWT(String jwt) throws UnsupportedEncodingException {

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(Secret.REFRESH_SECRET_KEY)// refreshToken 검증
                    .parseClaimsJws(jwt) // 파싱 및 검증, 실패 시 에러
                    .getBody();

        //    System.out.println(claims.get("userKey",Integer.class));
        //    System.out.println(claims.get("exp",Date.class));

        } catch (ExpiredJwtException e) { // 토큰이 만료되었을 경우
            System.out.println(e);
            return false;
        } catch (Exception e) { // 그외 에러났을 경우
            System.out.println(e);
            return false;
        }
        return true;
    }


    /*
    JWT에서 userIdx 추출
    @return int
    @throws BaseException
     */
    public int getUserKey() throws BaseException{
        //1. JWT 추출
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }
        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userKey",Integer.class);
    }

    public int getUserKeyFromRefreshToken(String refreshToken) throws BaseException {

        // 2. JWT parsing
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.REFRESH_SECRET_KEY)
                    .parseClaimsJws(refreshToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userKey",Integer.class);
    }


}
