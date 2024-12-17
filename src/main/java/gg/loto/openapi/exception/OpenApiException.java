package gg.loto.openapi.exception;

import gg.loto.global.exception.BusinessException;
import gg.loto.global.exception.ErrorCode;

public class OpenApiException extends BusinessException {
    public OpenApiException(ErrorCode errorCode){
        super(errorCode);
    }
}
