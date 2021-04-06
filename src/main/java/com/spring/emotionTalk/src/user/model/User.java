package com.spring.emotionTalk.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private int userKey;
    private String name;
    private String password;
    private String photoUrl;
    private String isDeleted;
}