package com.spring.emotionTalk.src.fcm;

import com.amazonaws.services.s3.model.GetBucketNotificationConfigurationRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.spring.emotionTalk.src.user.model.User;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import okhttp3.internal.ws.RealWebSocket;
import org.apache.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestPart;
import sun.awt.Symbol;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/emotiontalk-dcc75/messages:send";
    private final ObjectMapper objectMapper;


    public void sendMessageTo(String targetToken,String sender,String receiver, String contents,String img,
                                String emotion, Timestamp atTime) throws IOException, FirebaseMessagingException {

        String message = makeMessage(targetToken, sender,receiver,contents,img,emotion,atTime);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .build();

        Response response = client.newCall(request)
                .execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken,String sender,String receiver, String contents,String img,
                               String emotion, Timestamp atTime) throws JsonProcessingException{

        HashMap<String,String > map = new HashMap<String,String>();

        map.put("sender",sender);
        map.put("receiver",receiver);
        map.put("contents",contents);
        map.put("img",img);
        map.put("emotion",emotion);
        map.put("atTime",atTime.toString());

        FcmMessageDto fcmMessage = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .notification(FcmMessageDto.Notification.builder()
                                .body(contents)
                                .build())
                        .token(targetToken)
                        .data(map)
                        .build())
                .validate_only(false)
                .build();

        System.out.println(objectMapper.writeValueAsString(fcmMessage));
        return objectMapper.writeValueAsString(fcmMessage);
    }

    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "firebase/emotiontalk-dcc75-firebase-adminsdk-odl3m-de917e4c0a.json";

        GoogleCredentials googleCredentials = GoogleCredentials
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

        googleCredentials.refreshIfExpired();

        return googleCredentials.getAccessToken().getTokenValue();
    }


}


