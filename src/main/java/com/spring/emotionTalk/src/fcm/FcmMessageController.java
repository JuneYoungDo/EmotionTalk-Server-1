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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.spring.emotionTalk.config.BaseResponseStatus.*;

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
    public BaseResponse sendMessage(@RequestPart (value="imgFile", required = false) MultipartFile imgFile,
                                    @RequestParam (value="targetToken") String targetToken,
                                    @RequestParam (value="title") String title,
                                    @RequestParam (value="body") String body
                                    ) throws IOException, BaseException {

        int userKeyByJwt = jwtService.getUserKey();
        if(imgFile == null || targetToken == null || title == null || body == null)
            return new BaseResponse<>(INVALID_INPUT);

        S3Service s3Service = new S3Service();
        String img = s3Service.upload(imgFile);

        firebaseCloudMessageService.sendMessageTo(targetToken, title, body,img);

        String result = "전송하였습니다.";
        return new BaseResponse(result);
    }

}
