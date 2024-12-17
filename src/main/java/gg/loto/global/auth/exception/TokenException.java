package gg.loto.global.auth.exception;

import gg.loto.global.exception.BusinessException;
import gg.loto.global.exception.ErrorCode;

public class TokenException extends BusinessException {
    public TokenException(String msg, ErrorCode errorCode){
        super(msg + "\n" +  errorCode.getMessage(), errorCode);
    }
}
