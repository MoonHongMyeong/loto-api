package gg.loto.party.exception;

import gg.loto.global.exception.BusinessException;
import gg.loto.global.exception.ErrorCode;

public class VoteException extends BusinessException {
    public VoteException(String msg, ErrorCode errorCode){
        super(msg, errorCode);
    }

    public VoteException(ErrorCode errorCode){
        super(errorCode);
    }
}
