package cn.fenqing.spring.validation.exception;

import cn.fenqing.validation.bean.ValidationErrorInfo;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/7 10:42
 * @description 校验异常
 */
@Getter
public class ValidationException extends RuntimeException{

    public ValidationException(String message, Method method, List<ValidationErrorInfo> validationErrorInfos) {
        super(message);
        this.method = method;
        this.validationErrorInfos = validationErrorInfos;
    }

    private final Method method;

    private final List<ValidationErrorInfo> validationErrorInfos;

}
