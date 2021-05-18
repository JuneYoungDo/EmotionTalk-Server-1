package com.spring.emotionTalk.src.fcm;

import com.google.firebase.messaging.Message;
import lombok.*;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.util.Map;

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
        private Map data;
        private String token;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Notification{
//        private String title;
        private String body;
    }

}
