package gg.loto.raid.exception;

import gg.loto.global.exception.BusinessException;
import gg.loto.global.exception.ErrorCode;

public class RaidException extends BusinessException {
    public RaidException(ErrorCode errorCode){
        super(errorCode);
    }

    public RaidException(String msg, ErrorCode errorCode){
        super(msg, errorCode);
    }
}
