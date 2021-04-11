package com.spring.emotionTalk.src.auth.controller;

import com.spring.emotionTalk.config.BaseResponse;
import com.spring.emotionTalk.src.auth.dto.AuthDto;
import com.spring.emotionTalk.src.auth.service.social.GoogleOauth;
import com.spring.emotionTalk.src.auth.service.social.SocialOauth;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spring.emotionTalk.src.auth.helper.constants.SocialLoginType;
import com.spring.emotionTalk.src.auth.service.OauthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class OauthController {
    private final OauthService oauthService;
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 사용자로부터 SNS 로그인 요청을 Social Login Type 을 받아 처리
     * @param socialLoginType (GOOGLE, KAKAO)
     */
    @ResponseBody
    @GetMapping("/{socialLoginType}")
    public void socialLoginType(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType){

        logger.warn(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);
        oauthService.request(socialLoginType);
    }

    @ResponseBody
    @GetMapping("/{socialLoginType}/callback")
    public String callBack(
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
            @RequestParam(name = "code") String code,
            @RequestParam(name = "scope") String scope) {

        logger.warn(">> 소셜 로그인 API 서버로부터 받은 socialType :: {}", socialLoginType);
        logger.warn(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);

        String realCode = code + "&scope" + scope ;

        return oauthService.requestAccessToken(socialLoginType,code);
    }



}
