package com.spring.emotionTalk.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 200,200, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000,400, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001,400, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002,400, "유효하지 않은 JWT입니다."),
    EMPTY_ID_TOKEN(false,2003,400,"ID_TOKEN이 없습니다."),
    INVALID_INPUT(false,2004,400,"형식이 올바르지 않습니다."),
    INVALID_USER_KEY(false,2005,400,"유효하지 않은 userKey 입니다."),
    CAN_NOT_MY_SELF(false,2006,400,"나 자신을 지정할 수 없습니다."),
    // users
    USERS_EMPTY_USER_ID(false, 2010,0, "유저 아이디 값을 확인해주세요."),

    // [POST] /user
    POST_USERS_EMPTY_EMAIL(false, 2015,0, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016,0, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,0,"중복된 이메일입니다."),
    POST_USER_EXISTS_NAME(false,2018,0,"중복된 name 입니다."),

    // [POST] /user/login
    POST_USER_EMPTY_NAME(false,2019,0,"이름을 입력해주세요."),
    POST_USER_EMPTY_PWD(false,2020,0,"비밀번호를 입력해주세요."),
    USER_IS_NOT_AVAILABLE(false,2021,0,"삭제된 계정입니다."),
    FAILED_TO_LOGIN(false,2022,0,"정보가 일치하지 않습니다."),



    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000,0, "값을 불러오는데 실패하였습니다."),
    INVALID_USER_JWT(false,3000,400,"권한이 없습니다"),
    MODIFY_FAIL_MY_PROFILE(false,3001,400,"프로필 수정에 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013,0, "중복된 이메일입니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000,0, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001,0, "서버와의 연결에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(false, 4011,0, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012,0, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int status;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess,int status, int code, String message) {
        this.isSuccess = isSuccess;
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
