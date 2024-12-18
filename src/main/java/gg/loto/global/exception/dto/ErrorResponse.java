package gg.loto.global.exception.dto;

import gg.loto.global.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String message;
    private int httpStatus;
    private String code;
    private List<FieldError> errors;

    public ErrorResponse(final ErrorCode errorCode, final List<FieldError> errors){
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.errors = errors;
    }

    public ErrorResponse(final ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getHttpStatus();
        this.code = errorCode.getCode();
        this.errors = new ArrayList<>();
    }

    public static ErrorResponse of(final ErrorCode errorCode, final List<FieldError> errors){
        return new ErrorResponse(errorCode, errors);
    }

    public static ErrorResponse of(final ErrorCode errorCode){
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(MethodArgumentTypeMismatchException e) {
        final List<FieldError> errorFields = ErrorResponse.FieldError.of(e.getName(), e.getValue() == null ? "" : e.getValue().toString(), e.getErrorCode());
        return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE, errorFields);
    }

    public static ErrorResponse of(final ErrorCode errorCode, final BindingResult bindingResult) {
        return new ErrorResponse(errorCode, FieldError.from(bindingResult));
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError{
        private String field;
        private String value;
        private String reason;

        private FieldError(String field, String value, String reason){
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field, final String value, final String reason){
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> from(final BindingResult bindingResult){
            final List<org.springframework.validation.FieldError> errors = bindingResult.getFieldErrors();
            return errors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()
                    )).collect(Collectors.toList());
        }
    }
}
