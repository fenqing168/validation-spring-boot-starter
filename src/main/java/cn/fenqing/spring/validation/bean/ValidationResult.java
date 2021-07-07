package cn.fenqing.spring.validation.bean;

import lombok.Getter;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/7 9:22
 * @description 校验结果
 */
@Getter
public class ValidationResult {

    private boolean ok;

    private String message;

    public static ValidationResult ok(){
        ValidationResult validationResult = new ValidationResult();
        validationResult.ok = true;
        return validationResult;
    }

    public static ValidationResult error(String message){
        ValidationResult validationResult = new ValidationResult();
        validationResult.ok = false;
        validationResult.message = message;
        return validationResult;
    }

}
