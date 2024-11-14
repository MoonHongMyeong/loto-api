package gg.loto.global.auth;

import gg.loto.global.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String SESSION_KEY = "USER";
    private final HttpSession session;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isSessionUserClass = SessionUser.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isSessionUserClass;
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter
                                , @Nullable ModelAndViewContainer mavContainer
                                , @NonNull NativeWebRequest webRequest
                                , @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Object sessionUser = session.getAttribute(SESSION_KEY);
        if (sessionUser == null) {
            throw new RuntimeException("로그인이 필요한 서비스 입니다.");
        }
        return sessionUser;
    }
}
