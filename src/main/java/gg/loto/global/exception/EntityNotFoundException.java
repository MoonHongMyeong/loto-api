package gg.loto.global.exception;

public class EntityNotFoundException extends BusinessException{
    public EntityNotFoundException(ErrorCode errorCode){
        super(errorCode.getMessage(), errorCode);
    }
}
