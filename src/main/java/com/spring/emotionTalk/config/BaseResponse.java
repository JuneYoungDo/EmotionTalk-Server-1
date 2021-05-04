package com.spring.emotionTalk.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.spring.emotionTalk.src.user.model.PostLoginRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Timestamp;

import static com.spring.emotionTalk.config.BaseResponseStatus.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess","code", "message", "result","timeStamp"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isSuccess;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int code;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int status;
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private T result;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Timestamp timestamp;


    // 요청에 성공한 경우
    public BaseResponse(T result) {
    //    this.isSuccess = SUCCESS.isSuccess();
    //    this.message = SUCCESS.getMessage();
    //    this.code = SUCCESS.getCode();
    //    this.status = SUCCESS.getStatus();
        this.result = result;
    }
    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.code = status.getCode();
        this.message = status.getMessage();
        this.timestamp = status.getTimestamp();
    //    this.status = status.getStatus();
    }

    // jwt 토큰 요청
    public BaseResponse(String accessToken,String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken=refreshToken;
    }

}

