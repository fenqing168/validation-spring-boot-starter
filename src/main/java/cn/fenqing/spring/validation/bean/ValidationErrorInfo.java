package cn.fenqing.spring.validation.bean;

import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/7 10:43
 * @description 异常信息
 */
@Getter
@Setter
public class ValidationErrorInfo {

    private int index;

    private String parameterName;

    private List<String> message;

}
