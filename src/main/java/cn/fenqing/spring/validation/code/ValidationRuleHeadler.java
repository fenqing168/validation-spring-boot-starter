package cn.fenqing.spring.validation.code;

import cn.fenqing.validation.bean.ValidationResult;

import java.lang.annotation.Annotation;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/7 9:20
 * @description 校验规则
 */
public interface ValidationRuleHeadler<T extends Annotation> {

    /**
     * 校验方法
     * @param annotation
     * @param object
     * @return
     */
    ValidationResult validation(T annotation, Object object);

}
