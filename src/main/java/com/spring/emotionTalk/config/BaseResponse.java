package com.spring.emotionTalk.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.spring.emotionTalk.src.user.model.PostLoginRes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static com.spring.emotionTalk.config.BaseResponseStatus.SUCCESS;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess","status", "code", "message", "result"})
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
    private String jwt;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

    // 요청에 성공한 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.status = SUCCESS.getStatus();
        this.result = result;
    }

    // 요청에 실패한 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.status = status.getStatus();
        this.code = status.getCode();
    }

    // jwt 토큰 요청
    public BaseResponse(String jwt,String refreshToken) {
        this.jwt = jwt;
        this.refreshToken=refreshToken;
    }

}

