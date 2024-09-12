package org.example.expert.config.mock;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.domain.data.user.UserMockDataUtil;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MockAuthUserArgumentResolver extends AuthUserArgumentResolver {
    @Override
    public Object resolveArgument(
            @Nullable MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        return UserMockDataUtil.authUser(1L);
    }
}
