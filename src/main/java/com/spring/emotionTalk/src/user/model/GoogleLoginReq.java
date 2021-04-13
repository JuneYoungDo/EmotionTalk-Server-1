package com.spring.emotionTalk.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GoogleLoginReq {
    private String idToken;
    public GoogleLoginReq() {}
}
