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

    public List<GetUserRes> getUsers(String email){
        return this.jdbcTemplate.query("select * from UserInfo where email =?",
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                email);
    }

    public GetUserRes getUser(int userIdx){
        return this.jdbcTemplate.queryForObject("select * from UserInfo where userIdx = ?",
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("userName"),
                        rs.getString("ID"),
                        rs.getString("Email"),
                        rs.getString("password")),
                userIdx);
    }


    public int createUser(PostUserReq postUserReq){
        this.jdbcTemplate.update("insert into UserInfo (userName, ID, password, email) VALUES (?,?,?,?)",
                new Object[]{postUserReq.getUserName(), postUserReq.getId(), postUserReq.getPassword(), postUserReq.getEmail(),}
        );
        return this.jdbcTemplate.queryForObject("select last_insert_id()",int.class);
    }

    public int checkEmail(String email){
        return this.jdbcTemplate.queryForObject("select exists(select email from UserInfo where email = ?)",
                int.class,
                email);

    }



}
