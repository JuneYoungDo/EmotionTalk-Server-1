package com.spring.emotionTalk.src.user;


import com.spring.emotionTalk.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public GetUserRes getUser(int userKey){
        return this.jdbcTemplate.queryForObject("select userKey,name, email,photoUrl from user where userKey = ? " +
                                                        "and isDeleted ='N';",
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userKey"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("photoUrl")),
                userKey);
    }


    public int createUser(PostUserReq postUserReq){
        this.jdbcTemplate.update("INSERT INTO `realTalk`.`user` (`name`, `pwd`) VALUES (?, ?);",
                new Object[]{postUserReq.getName(), postUserReq.getPassword(),}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int createUser(GoogleUserReq googleUserReq){
        this.jdbcTemplate.update("insert into user (name,googleId,email) VALUES (?,?,?)",
                new Object[]{
                        googleUserReq.getName(),
                        googleUserReq.getId(),
                        googleUserReq.getEmail()}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public void insertRefreshToken(int userKey,String refreshToken){
        this.jdbcTemplate.update("UPDATE user SET refreshToken = ? WHERE (userKey = ?);",
                new Object[]{
                        refreshToken,userKey}
        );
    }

    public int checkGoogleEmail(String googleEmail) {
        return this.jdbcTemplate.queryForObject("select exists(select email from user where email = ? and isDeleted = 'N')",
                int.class, googleEmail);
    }

    public int getUserKeyByGoogleEmail(String googleEmail) {
        String query = "select userKey from user where email = ?";

        return this.jdbcTemplate.queryForObject(query, int.class, googleEmail);
    }


    public int checkEmail(String email){
        return this.jdbcTemplate.queryForObject("select exists(select email from UserInfo where email = ?)",
                int.class,
                email);

    }
    public int checkName(String name){
        return this.jdbcTemplate.queryForObject("select exists(select name from user where name = ?)",
                int.class,
                name);

    }
    public User getPwd(PostLoginReq postLoginReq){

        String getPwdQuery = "select userKey,name, pwd,photoUrl,isDeleted from user where name = ?;";
        String getPwdParams = postLoginReq.getName();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userKey"),
                        rs.getString("name"),
                        rs.getString("pwd"),
                        rs.getString("photoUrl"),
                        rs.getString("isDeleted")
                ),
                getPwdParams
        );
    }

    public int patchMyProfile(PatchUserReq patchUserReq,int userKey){
        String patchMyProfileQuery = "UPDATE user SET name = ?, photoUrl = ? WHERE userKey = ?;";
        Object[] patchMyProfileParams = new Object[]{patchUserReq.getUserName(), patchUserReq.getPhotoUrl(),userKey};
        return this.jdbcTemplate.update(patchMyProfileQuery,patchMyProfileParams);
    }



}
