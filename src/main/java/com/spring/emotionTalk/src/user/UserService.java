package com.spring.emotionTalk.src.user;

import com.spring.emotionTalk.config.BaseException;
import com.spring.emotionTalk.config.BaseResponse;
import com.spring.emotionTalk.config.secret.Secret;
import com.spring.emotionTalk.src.user.model.*;
import com.spring.emotionTalk.utils.AES128;
import com.spring.emotionTalk.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sound.midi.Patch;
import javax.sql.DataSource;

import static com.spring.emotionTalk.config.BaseResponseStatus.*;

// Service Create, Update, Delete 의 로직 처리
@Service
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService;

    }

    // creatUser
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        //중복
        if(userProvider.checkName(postUserReq.getName()) ==1){
            throw new BaseException(POST_USER_EXISTS_NAME);
        }

        String pwd;
        try{
            //암호화
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword());
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }

        int userIdx = userDao.createUser(postUserReq);

        return new PostUserRes(userIdx);
    }


    //POST
    public PostLoginRes createUserByGoogle(GoogleUserReq googleUserReq) throws BaseException {
        try {
            int userKey = userDao.createUser(googleUserReq);

            String jwt = jwtService.createJwt(userKey);
            String refreshToken = jwtService.createRefreshToken(userKey);

            userDao.insertRefreshToken(userKey,refreshToken);

            return new PostLoginRes(jwt,refreshToken);
        }catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int findUserByGoogleEmail(String googleEmail) {
        if(userProvider.checkGoogleEmail(googleEmail) ==1){
            return 1;
        }
        return 0;
    }

    //PATCH
    public void patchMyProfile(PatchUserReq patchUserReq,int userKey,String filePath){
        try{
            int result = userDao.patchMyProfile(patchUserReq,userKey,filePath);
            if(result == 0)
            {
                throw new BaseException(MODIFY_FAIL_MY_PROFILE);
            }
        } catch (BaseException exception) {
            exception.printStackTrace();
        }
    }

    public BaseResponse chmodFriends(int userKey, int anotherKey){
        String result = "";
        if(userDao.isExistFriendList(userKey,anotherKey)==0) {
            userDao.postFriendList(userKey,anotherKey); // 친구 추가
            result = "친구목록에 추가하였습니다.";
            return new BaseResponse(result);
        }
        else {
            // 친구 목록에 있을 때
            if(userDao.isDeletedFriend(userKey,anotherKey) == 'Y'){
                userDao.chmodFriendList(userKey,anotherKey);    // 친구 추가
                result = "친구목록에 추가하였습니다.";
                return new BaseResponse(result);
            }
            else {
                userDao.chmodFriendList(userKey,anotherKey);    // 친구 삭제
                result = "친구목록에서 삭제하였습니다.";
                return new BaseResponse(result);
            }
        }


    }

}
