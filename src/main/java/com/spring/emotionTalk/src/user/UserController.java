    package com.spring.emotionTalk.src.user;

import com.spring.emotionTalk.src.auth.dto.AuthDto;
import com.spring.emotionTalk.src.auth.helper.constants.SocialLoginType;
import com.spring.emotionTalk.src.auth.service.OauthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.spring.emotionTalk.config.BaseException;
import com.spring.emotionTalk.config.BaseResponse;
import com.spring.emotionTalk.src.user.model.*;
import com.spring.emotionTalk.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;


import static com.spring.emotionTalk.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app")
public class UserController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OauthService oauthService;

    @Autowired
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;

    public UserController(OauthService oauthService, UserProvider userProvider, UserService userService, JwtService jwtService){
        this.oauthService = oauthService;
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    /**
     * 회원 1명 조회 API
     * [GET] /users/:userIdx
     * @return BaseResponse<GetUserRes>
    // Path-variable
    @ResponseBody
    @GetMapping("/user/{userIdx}") // (GET) 127.0.0.1:9000/app/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // Get Users
        GetUserRes getUserRes = userProvider.getUser(userIdx);
        return new BaseResponse<>(getUserRes);
    }*/

    /**
     * 회원가입 API
     * [POST] /user
     * @return BaseResponse<PostUserRes>
     */
    // Body
    @ResponseBody
    @PostMapping("/user")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) throws BaseException {
        if(postUserReq.getName() == null){
            return new BaseResponse<>(POST_USER_EMPTY_NAME);
        }
        if(postUserReq.getPassword() == null){
            return new BaseResponse<>(POST_USER_EMPTY_PWD);
        }
        try{
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 로그인 API
     * [POST] /user/login
     * @return BaseResponse<PostUserRes>
     */
    @ResponseBody
    @PostMapping("/user/login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) throws BaseException {
        try{
            if(postLoginReq.getName() == null) {
                return new BaseResponse<>(POST_USER_EMPTY_NAME);
            }
            if(postLoginReq.getPassword() == null) {
                return new BaseResponse<>(POST_USER_EMPTY_PWD);
            }
            PostLoginRes postLoginRes = userProvider.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);

        }catch(BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 소셜 로그인 API
     * [POST] /{socialLoginType}/login
     * @param socialLoginType (GOOGLE, KAKAO)
     */
    @ResponseBody
    @PostMapping("/{socialLoginType}/login")
    public BaseResponse<PostLoginRes> getInfo(
            @RequestBody GoogleLoginReq googleLoginReq,
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) throws GeneralSecurityException, IOException {
        String token = googleLoginReq.getIdToken();
        if (token == "") {
            return new BaseResponse<>(EMPTY_ID_TOKEN);
        } else {

            AuthDto.GoogleProfileRes googleProfileRes = oauthService.loadInfo(socialLoginType, token);

            GoogleUserReq googleUserReq = new GoogleUserReq();

            googleUserReq.setId(googleProfileRes.getId());
            googleUserReq.setEmail(googleProfileRes.getEmail());
            googleUserReq.setName(googleProfileRes.getName());

            PostLoginRes postLoginRes = new PostLoginRes();
            if (userService.findUserByGoogleEmail(googleUserReq.getEmail()) == 1) {
                System.out.println("loginUser");
                try {
                    postLoginRes = userProvider.logIn(googleUserReq.getEmail());
                    return new BaseResponse<>(postLoginRes.getAccessToken(),postLoginRes.getRefreshToken());
                } catch (BaseException exception) {
                    return new BaseResponse<>(exception.getStatus());
                }
            } else {
                System.out.println("createUser");
                try {
                    postLoginRes = userService.createUserByGoogle(googleUserReq);
                    return new BaseResponse<>(postLoginRes.getAccessToken(), postLoginRes.getRefreshToken());
                } catch (BaseException exception) {
                    return new BaseResponse<>(exception.getStatus());
                }
            }
        }
    }

    /**
     * 내 프로필 확인 API
     * [GET] /user/:userIdx
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/user")
    public BaseResponse<GetUserRes> getMyProfile(){
        try{
            //jwt에서 idx 추출.
            int userKeyByJwt = jwtService.getUserKey();
            // Get Users
            GetUserRes getUserRes = userProvider.getUser(userKeyByJwt);
            return new BaseResponse<>(getUserRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 내 프로필 수정 API
     * [PATCH] /user
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @PatchMapping("/user")
    public BaseResponse<GetUserRes> patchMyProfile(@RequestBody PatchUserReq patchUserReq){
        try{
            //jwt에서 idx 추출.
            int userKeyByJwt = jwtService.getUserKey();

            if(patchUserReq.getUserName() == null || patchUserReq.getPhotoUrl() == null)
                return new BaseResponse<>(INVALID_INPUT);

            userService.patchMyProfile(patchUserReq,userKeyByJwt);

            String result = "수정에 성공하였습니다.";
            return new BaseResponse(result);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
