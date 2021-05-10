package com.spring.emotionTalk.config;

import lombok.Getter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 에러 코드 관리
 */

@Getter
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */

    SUCCESS(true, 200, "요청에 성공하였습니다.",new Timestamp(System.currentTimeMillis())),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요.",new Timestamp(System.currentTimeMillis())),
    EMPTY_JWT(false,2001, "JWT를 입력해주세요.",new Timestamp(System.currentTimeMillis())),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다.",new Timestamp(System.currentTimeMillis())),
    EMPTY_ID_TOKEN(false,2003,"ID_TOKEN이 없습니다.",new Timestamp(System.currentTimeMillis())),
    INVALID_INPUT(false,2004,"형식이 올바르지 않습니다.",new Timestamp(System.currentTimeMillis())),
    INVALID_USER_KEY(false,2005,"유효하지 않은 userKey 입니다.",new Timestamp(System.currentTimeMillis())),
    CAN_NOT_MY_SELF(false,2006,"나 자신을 지정할 수 없습니다.",new Timestamp(System.currentTimeMillis())),
    EMPTY_REFRESH_TOKEN(false,2007,"REFRESH_TOKEN이 없습니다.",new Timestamp(System.currentTimeMillis())),
    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요.",new Timestamp(System.currentTimeMillis())),

    // [POST] /user
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요.",new Timestamp(System.currentTimeMillis())),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요.",new Timestamp(System.currentTimeMillis())),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다.",new Timestamp(System.currentTimeMillis())),
    POST_USER_EXISTS_NAME(false,2018,"중복된 name 입니다.",new Timestamp(System.currentTimeMillis())),

    // [POST] /user/login
    POST_USER_EMPTY_NAME(false,2019,"이름을 입력해주세요.",new Timestamp(System.currentTimeMillis())),
    POST_USER_EMPTY_PWD(false,2020,"비밀번호를 입력해주세요.",new Timestamp(System.currentTimeMillis())),
    USER_IS_NOT_AVAILABLE(false,2021,"삭제된 계정입니다.",new Timestamp(System.currentTimeMillis())),
    FAILED_TO_LOGIN(false,2022,"정보가 일치하지 않습니다.",new Timestamp(System.currentTimeMillis())),



    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다.",new Timestamp(System.currentTimeMillis())),
    INVALID_USER_JWT(false,3001,"권한이 없습니다",new Timestamp(System.currentTimeMillis())),
    MODIFY_FAIL_MY_PROFILE(false,3002,"프로필 수정에 실패하였습니다.",new Timestamp(System.currentTimeMillis())),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다.",new Timestamp(System.currentTimeMillis())),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다.",new Timestamp(System.currentTimeMillis())),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다.",new Timestamp(System.currentTimeMillis())),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다.",new Timestamp(System.currentTimeMillis())),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.",new Timestamp(System.currentTimeMillis()));


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
   // private final int status;
    private final int code;
    private final String message;
    private final Timestamp timestamp;

    private BaseResponseStatus(boolean isSuccess, int code, String message, Timestamp timestamp) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }
}
