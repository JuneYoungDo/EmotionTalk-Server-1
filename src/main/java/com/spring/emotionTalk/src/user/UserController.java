    package com.spring.emotionTalk.src.user;

import com.spring.emotionTalk.src.S3Service;
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
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;
import java.io.IOException;

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
    @Autowired
    private final S3Service s3Service;

    public UserController(OauthService oauthService, UserProvider userProvider, UserService userService,
                          JwtService jwtService, S3Service s3Service){
        this.oauthService = oauthService;
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
    }

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
     * refreshToken을 통한 accessToken 재발급
     * [POST] /{socialLoginType}/login/refresh
     * @param socialLoginType (GOOGLE, KAKAO)
     */
    @ResponseBody
    @PostMapping("/{socialLoginType}/login/refresh")
    public BaseResponse<PostLoginRes> renewalToken(
            @RequestBody GetRefreshTokenReq getRefreshTokenReq,
            @PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) throws GeneralSecurityException, IOException, BaseException {

        String refreshToken = getRefreshTokenReq.getRefreshToken();
        if (refreshToken == "") {
            return new BaseResponse<>(EMPTY_REFRESH_TOKEN);
        } else {
            // refreshToken 유효성 검사
            if(jwtService.verifyJWT(refreshToken) == true){
                PostLoginRes postLoginRes=new PostLoginRes();

                int userKey = jwtService.getUserKeyFromRefreshToken(refreshToken);
                String tokenInDB = userProvider.getRefreshToken(userKey);

                // refreshToken과 DB refreshToken 비교
                if(refreshToken.equals(tokenInDB)){
                    String accessToken = jwtService.createJwt(userKey);
                    postLoginRes.setAccessToken(accessToken);
                    return new BaseResponse<>(postLoginRes.getAccessToken(),postLoginRes.getRefreshToken());
                }
                else{
                    return new BaseResponse<>(INVALID_JWT);
                }

            }
            else{
                return new BaseResponse<>(INVALID_JWT);
            }
        }
    }



    /**
     * 내 프로필 수정 API
     * [PATCH] /user
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @PatchMapping("/user")
    public BaseResponse<GetUserRes> patchMyProfile(@RequestPart (value = "file", required = false) MultipartFile multipartFile
                                                   ,@RequestParam (value = "userName") PatchUserReq patchUserReq){
        try{
            int userKeyByJwt = jwtService.getUserKey();

            if(patchUserReq.getUserName() == null || multipartFile == null)
                return new BaseResponse<>(INVALID_INPUT);

            S3Service S3Service = new S3Service();
            String filePath = S3Service.upload(multipartFile);

            userService.patchMyProfile(patchUserReq, userKeyByJwt,filePath);

            String result = "수정에 성공하였습니다.";
            return new BaseResponse(result);
        } catch(BaseException baseException){
            return new BaseResponse(baseException.getStatus());
        }catch(IOException ioException){
            return new BaseResponse(ioException.getCause());
        }
    }

    /**
     * 프로필 확인 API
     * [GET] /user?userKey={userKey}
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/user")
    public BaseResponse<GetUserRes> getUserProfile(@RequestParam(value = "userKey") int userKey){
        try{
            //jwt에서 idx 추출.
            int userKeyByJwt = jwtService.getUserKey();

            GetUserRes getUserRes = userProvider.getUser(userKey);
            if(getUserRes == null)
                return new BaseResponse<>(INVALID_USER_KEY);

            return new BaseResponse<>(getUserRes);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저 찾기
     * [GET] /user/id?userName={name}
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/user/id")
    public BaseResponse<List<GetUserFindRes>> getUserSearch(@RequestParam(value = "userName") String name){
        try{
            //jwt에서 idx 추출.
            int userKeyByJwt = jwtService.getUserKey();

            if(name.equals(""))
                return new BaseResponse("No Data");

            List<GetUserFindRes> getUserFindRes = userProvider.getUserSearchList(name);

            return new BaseResponse<>(getUserFindRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 친구 추가
     * [GET] /user/fd?userKey={userKey}
     * @return BaseResponse<GetUserRes>
     */
    @ResponseBody
    @GetMapping("/user/fd")
    public BaseResponse<GetUserRes> chmodFriend(@RequestParam(name = "userKey") int userKey){
        try{
            //jwt에서 idx 추출.
            int userKeyByJwt = jwtService.getUserKey();

            GetUserRes getUserRes = userProvider.getUser(userKey);
            if(getUserRes == null)
                return new BaseResponse<>(INVALID_USER_KEY);

            int anotherKey = getUserRes.getUserKey();

            if(userKeyByJwt == anotherKey) {
                return new BaseResponse<>(CAN_NOT_MY_SELF);
            }
            // 친구 추가 -> 리스트에 있는지 -> 없으면 추가
            //                          -> 있으면 isDeleted 확인 -> 'Y' -> 'N' 으로 바꿈
            //                                                  -> 'N' -> 'Y' 로 바꿈
            BaseResponse baseResponse = userService.chmodFriends(userKeyByJwt,anotherKey);

            return baseResponse;
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 친구 목록 보기
     * [GET] /user/fdList
     * @return BaseResponse<GetUserFdList>
     */
    @ResponseBody
    @GetMapping("/user/fdList")
    public BaseResponse<List<GetUserFriendListRes>> getUserFriendList(){
        try{
            //jwt에서 idx 추출.
            int userKeyByJwt = jwtService.getUserKey();

            List<GetUserFriendListRes> getUserFriendListRes = userProvider.getUserFriendList(userKeyByJwt);

            return new BaseResponse<>(getUserFriendListRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }



}
