package gg.loto.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private ErrorCode errorCode;

    public BusinessException(String msg, ErrorCode code){
        super(msg);
        this.errorCode = code;
    }

    public BusinessException(ErrorCode code){
        super(code.getMessage());
        this.errorCode = code;
    }
}
