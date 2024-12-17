package gg.loto.global.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    // common
    INTERNAL_SERVER_ERROR(500, "Internal Server Error", "알 수 없는 에러\n 관리자에게 연락해주세요."),
    INVALID_INPUT_VALUE(400, "Invalid Input Value", "잘못된 input 값 입니다."),
    INVALID_TYPE_VALUE(400, "Invalid Type Value", "잘못된 type 입니다."),
    ENTITY_NOT_FOUND(404,"Entity Not Found", "Entity 를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "허용되지 않은 메소드"),
    HANDLE_ACCESS_DENIED(403, "Handle Access Denied", "엑세스가 거부되었습니다."),

    // token
    TOKEN_EXPIRES(401, "Token is expired", "토큰이 만료되었습니다."),
    TOKEN_INVALID(403, "Invalid token", "잘못된 토큰입니다.");

    private final int httpStatus;
    private final String code;
    private final String message;

    ErrorCode(int httpStatus, String code, String message){
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
