package com.spring.emotionTalk.src.fcm;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostMessageReq {
    private String targetToken;
    private String title;
    private String body;
    private String image;
}
