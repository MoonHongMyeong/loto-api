package gg.loto.character.exception;

import gg.loto.global.exception.BusinessException;
import gg.loto.global.exception.ErrorCode;

public class CharacterException extends BusinessException {
    public CharacterException(ErrorCode errorCode){
        super(errorCode);
    }
}
