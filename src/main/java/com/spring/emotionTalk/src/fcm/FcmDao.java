package com.spring.emotionTalk.src.fcm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class FcmDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public String getDeviceTokenByUserKey(int userKey) {
        String query = "select deviceToken from user where userKey = ?";

        return this.jdbcTemplate.queryForObject(query, String.class, userKey);
    }

    public String getUserName(int userKey){
        return this.jdbcTemplate.queryForObject("select name from user where userKey =?;",
                String.class, userKey);
    }

    public int isValidUserKey(int userKey) {
        String isValidUserKeyQuery = "select exists(select userKey from user where userKey = ?);";
        return this.jdbcTemplate.queryForObject(isValidUserKeyQuery,int.class,userKey);
    }
}
