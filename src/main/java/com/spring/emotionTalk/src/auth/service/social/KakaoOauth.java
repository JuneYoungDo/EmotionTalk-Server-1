package com.spring.emotionTalk.src.auth.service.social;

import com.spring.emotionTalk.src.auth.dto.AuthDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoOauth implements SocialOauth{
    @Override
    public String getOauthRedirectURL(){
        return "";
    }
    @Override
    public String requestAccessToken(String code) { return "";}

    public AuthDto.GoogleProfileRes userInfoGoogle(AuthDto.AccessTokenReq dto){ return AuthDto.GoogleProfileRes.builder().build();}
}
