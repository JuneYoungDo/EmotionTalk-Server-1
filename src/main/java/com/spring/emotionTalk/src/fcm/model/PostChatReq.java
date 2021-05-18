package com.spring.emotionTalk.src.fcm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostChatReq {
    private Integer anotherKey;
    private String contents;
    private String emotion;
}
