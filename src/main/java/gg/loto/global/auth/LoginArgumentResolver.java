package gg.loto.global.auth;

import gg.loto.auth.domain.Token;
import gg.loto.auth.repository.TokenRepository;
import gg.loto.global.auth.provider.JwtTokenProvider;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
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
import java.time.LocalDateTime;

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
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        Long userId = (Long) authentication.getPrincipal();
        Token token = tokenRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("토큰 정보가 존재하지 않습니다."));

        if (token.isAccessTokenExpired()) {
            if (token.isRefreshTokenExpired()) {
                throw new RuntimeException("리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
            }
            throw new RuntimeException("액세스 토큰이 만료되었습니다. 토큰을 재발급 받아주세요.");
        }

        return token.getUser();
    }
}
