package gg.loto.raid.exception;

import gg.loto.global.exception.BusinessException;
import gg.loto.global.exception.ErrorCode;

public class RaidException extends BusinessException {
    public RaidException(ErrorCode errorCode){
        super(errorCode);

    }
}
