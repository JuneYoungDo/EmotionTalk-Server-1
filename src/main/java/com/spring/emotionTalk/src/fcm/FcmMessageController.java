package com.spring.emotionTalk.src.fcm;

import com.spring.emotionTalk.config.BaseException;
import com.spring.emotionTalk.config.BaseResponse;
import com.spring.emotionTalk.src.S3Service;
import com.spring.emotionTalk.src.auth.service.OauthService;
import com.spring.emotionTalk.src.user.model.PostUserReq;
import com.spring.emotionTalk.src.user.model.PostUserRes;
import com.spring.emotionTalk.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.spring.emotionTalk.config.BaseResponseStatus.POST_USER_EMPTY_NAME;
import static com.spring.emotionTalk.config.BaseResponseStatus.POST_USER_EMPTY_PWD;

@RestController
@RequestMapping("/app")
public class FcmMessageController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final OauthService oauthService;
    @Autowired
    private final JwtService jwtService;
    @Autowired
    private final S3Service s3Service;
    @Autowired
    private final FirebaseCloudMessageService firebaseCloudMessageService;

    public FcmMessageController(OauthService oauthService, JwtService jwtService, S3Service s3Service,
                                FirebaseCloudMessageService firebaseCloudMessageService){
        this.oauthService = oauthService;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
        this.firebaseCloudMessageService = firebaseCloudMessageService;
    }

    @ResponseBody
    @PostMapping("/chat")
    public String sendMessage(@RequestBody PostMessageReq postMessageReq) throws IOException {

        firebaseCloudMessageService.sendMessageTo(postMessageReq.getTargetToken(),
                postMessageReq.getTitle(), postMessageReq.getBody());


        return "";
    }

}
