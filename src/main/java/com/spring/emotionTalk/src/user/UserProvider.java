package com.spring.emotionTalk.src.user;


import com.spring.emotionTalk.config.BaseException;
import com.spring.emotionTalk.config.BaseResponseStatus;
import com.spring.emotionTalk.config.secret.Secret;
import com.spring.emotionTalk.src.user.model.*;
import com.spring.emotionTalk.utils.AES128;
import com.spring.emotionTalk.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

//Provider : Read의 비즈니스 로직 처리
@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }


    public GetUserRes getUser(int userKey) {
        if(userDao.isValidUserKey(userKey) == 0)
        {
            GetUserRes getUserRes = null;
            return getUserRes;
        }
        else {
            GetUserRes getUserRes = userDao.getUser(userKey);
            return getUserRes;
        }
    }

    public int checkEmail(String email) {
        return userDao.checkEmail(email);
    }

    public int checkName(String name) {
        return userDao.checkName(name);
    }

    // login
    public PostLoginRes login(PostLoginReq postLoginReq)throws BaseException{
        User user = userDao.getPwd(postLoginReq);
        String password;
        try{
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword());
        }catch(Exception ignored){
            throw new BaseException(BaseResponseStatus.PASSWORD_DECRYPTION_ERROR);
        }
        if(postLoginReq.getPassword().equals(password)){
            if(user.getIsDeleted().equals("N"))
            {
                   int userKey = userDao.getPwd(postLoginReq).getUserKey();
                   String jwt = jwtService.createJwt(userKey);
                   String refreshToken = jwtService.createRefreshToken(userKey);
                   return new PostLoginRes(jwt,refreshToken);
            }
            else{
                throw new BaseException(BaseResponseStatus.USER_IS_NOT_AVAILABLE);
            }
        }
        else
            throw new BaseException(BaseResponseStatus.USER_IS_NOT_AVAILABLE);
    }

    public int checkGoogleEmail(String googleEmail) {
        return userDao.checkGoogleEmail(googleEmail);
    }


    public PostLoginRes logIn(String googleEmail) throws BaseException{
        int userKey = userDao.getUserKeyByGoogleEmail(googleEmail);

        String jwt = jwtService.createJwt(userKey);
        String refreshToken = jwtService.createRefreshToken(userKey);

        userDao.insertRefreshToken(userKey,refreshToken);

        return new PostLoginRes(jwt,refreshToken);
    }

    public List<GetUserFriendListRes> getUserFriendList(int userKey) {
        List<GetUserFriendListRes> getUserFriendListRes = userDao.getUserFriendList(userKey);

        return getUserFriendListRes;
    }


}
