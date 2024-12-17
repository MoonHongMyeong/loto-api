package gg.loto.global.auth;

import gg.loto.auth.domain.Token;
import gg.loto.auth.repository.TokenRepository;
import gg.loto.global.auth.exception.TokenException;
import gg.loto.global.exception.EntityNotFoundException;
import gg.loto.global.exception.ErrorCode;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.nio.file.AccessDeniedException;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isUserClass = User.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter
                                , @Nullable ModelAndViewContainer mavContainer
                                , @NonNull NativeWebRequest webRequest
                                , @Nullable WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("로그인이 필요한 서비스입니다.");
        }

        Long userId = (Long) authentication.getPrincipal();
        Token token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.TOKEN_NOT_FOUND));

        if (token.isAccessTokenExpired()) {
            if (token.isRefreshTokenExpired()) {
                throw new TokenException(token.getRefreshToken(), ErrorCode.EXPIRED_REFRESH_TOKEN);
            }
            throw new TokenException(token.getAccessToken(), ErrorCode.EXPIRED_TOKEN);
        }

        return token.getUser();
    }
}
