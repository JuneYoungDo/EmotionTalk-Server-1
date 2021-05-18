package com.spring.emotionTalk.src.fcm;

import com.spring.emotionTalk.src.user.UserDao;
import com.spring.emotionTalk.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class FcmProvider {

    private final UserDao userDao;
    private final FcmDao fcmDao;
    private final JwtService jwtService;

    private JdbcTemplate jdbcTemplate;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Autowired
    public FcmProvider(UserDao userDao,
                       FcmDao fcmDao,JwtService jwtService) {
        this.userDao = userDao;
        this.fcmDao = fcmDao;
        this.jwtService = jwtService;
    }

    public String getDeviceTokenByUserKey(int userKey){
        return fcmDao.getDeviceTokenByUserKey(userKey);
    }

    public String getUserNameByUserKey(int userKey){return fcmDao.getUserName(userKey);}

    public int isValidUserKey(int userKey){ return fcmDao.isValidUserKey(userKey);}

}
