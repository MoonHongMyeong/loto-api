package gg.loto.party.exception;

import gg.loto.global.exception.BusinessException;
import gg.loto.global.exception.ErrorCode;

public class PartyException extends BusinessException {
    public PartyException(ErrorCode errorCode){
        super(errorCode);
    }
}
