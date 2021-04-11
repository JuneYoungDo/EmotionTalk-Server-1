package com.spring.emotionTalk.src.auth.dto;

import io.swagger.annotations.ApiModel;
import lombok.*;

public class AuthDto {
    @ApiModel ("Client에서 토큰 받기")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class AccessTokenReq {

        private String accessToken;
    }

    @ApiModel("구글 유저정보 GET")
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor(access = AccessLevel.PUBLIC)
    public static class GoogleProfileRes {

        private String id;
        private String email;
        private String name;
    }
}
