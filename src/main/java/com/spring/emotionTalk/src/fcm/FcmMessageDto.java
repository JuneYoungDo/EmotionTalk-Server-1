package com.spring.emotionTalk.src.fcm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class FcmMessageDto {
    private boolean validate_only;
    private Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Message{
        Notification notification;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Notification{
        private String title;
        private String body;
        private String image;
    }
}
