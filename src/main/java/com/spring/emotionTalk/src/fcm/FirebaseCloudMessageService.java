package com.spring.emotionTalk.src.fcm;

import com.amazonaws.services.s3.model.GetBucketNotificationConfigurationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.spring.emotionTalk.src.user.model.User;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestPart;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {

    private final String API_URL = "https://fcm.googleapis.com/v1/projects/emotiontalk-dcc75/messages:send";
    private final ObjectMapper objectMapper;

    public void sendMessageTo(String targetToken, String title, String body,String img) throws IOException {
        String message = makeMessage(targetToken, title, body,img);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .build();

        Response response = client.newCall(request)
                .execute();

        System.out.println(response.body().string());
    }

    private String makeMessage(String targetToken, String title, String body,String img) throws JsonProcessingException {
        FcmMessageDto fcmMessage = FcmMessageDto.builder()
                .message(FcmMessageDto.Message.builder()
                        .token(targetToken)
                        .notification(FcmMessageDto.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(img)
                                .build()
                        )
                        .build()
                )
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

        System.out.println("start");
        System.out.println(googleCredentials.getAccessToken().getTokenValue());

        return googleCredentials.getAccessToken().getTokenValue();
    }


}


