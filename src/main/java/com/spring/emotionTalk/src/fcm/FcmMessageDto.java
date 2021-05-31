package com.spring.emotionTalk.src.fcm;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.firebase.auth.internal.FirebaseCustomAuthToken;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.Message;
import com.nimbusds.jose.Payload;
import com.turo.pushy.apns.util.ApnsPayloadBuilder;
import lombok.*;
import okhttp3.Headers;
import org.apache.http.Header;
import org.codehaus.jackson.annotate.JsonAutoDetect;

import javax.xml.crypto.Data;
import java.sql.Timestamp;
import java.util.Map;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class FcmMessageDto {
    private boolean validate_only;
    Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Message{
        Notification notification;
        private Map data;
        private String token;
        ApnsConfig apns;
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
