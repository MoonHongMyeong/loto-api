package gg.loto.global.auth;

import gg.loto.global.auth.dto.SessionUser;
import gg.loto.global.auth.provider.JwtTokenProvider;
import gg.loto.user.domain.User;
import gg.loto.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginArgumentResolver implements HandlerMethodArgumentResolver {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
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
        String authorizationHeader = webRequest.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        String token = authorizationHeader.substring(7);

        if (!jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        Long userId = jwtTokenProvider.getUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        return user;
    }
}
