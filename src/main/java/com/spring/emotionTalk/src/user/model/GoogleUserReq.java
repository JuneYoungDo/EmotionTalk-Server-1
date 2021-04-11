package com.spring.emotionTalk.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GoogleUserReq {
    public GoogleUserReq(){}

    private String id;
    private String email;
    private String name;
}
