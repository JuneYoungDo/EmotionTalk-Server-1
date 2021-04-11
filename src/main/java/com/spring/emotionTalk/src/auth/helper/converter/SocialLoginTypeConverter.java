package com.spring.emotionTalk.src.auth.helper.converter;

import com.spring.emotionTalk.src.auth.helper.constants.SocialLoginType;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration
public class SocialLoginTypeConverter implements Converter<String, SocialLoginType> {
    @Override
    public SocialLoginType convert(String s)
    {
        return SocialLoginType.valueOf(s.toUpperCase());
    }
}
