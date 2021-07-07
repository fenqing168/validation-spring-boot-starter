package cn.fenqing.spring.validation.annotation;

import java.lang.annotation.*;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/6 17:24
 * @description 激活校验
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ActivateValidation {
}
