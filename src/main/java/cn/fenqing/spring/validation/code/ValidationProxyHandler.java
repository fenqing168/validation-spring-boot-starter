package cn.fenqing.spring.validation.code;

import cn.fenqing.validation.bean.ValidationErrorInfo;
import cn.fenqing.validation.bean.ValidationResult;
import cn.fenqing.validation.exception.ValidationException;
import cn.fenqing.validation.utils.ReflectUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/7 9:16
 * @description 校验处理器
 */
public class ValidationProxyHandler implements InvocationHandler {

    private final Object oldObject;

    private final Map<Method, List<Annotation>[]> methodMap;

    private final Map<Annotation, Class<Annotation>> annotationClassMap;

    private static Method METHOD;

    static {
        try {
            METHOD = ValidationRuleHeadler.class.getMethod("validation", Annotation.class, Object.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public ValidationProxyHandler(Object oldObject, Map<Method, List<Annotation>[]> methodMap) {
        this.oldObject = oldObject;
        this.methodMap = methodMap;
        annotationClassMap = new HashMap<>();
        methodMap.forEach((k, v) -> {
            for (List<Annotation> annotations : v) {
                annotations.forEach(annotation -> annotationClassMap.put(annotation, ReflectUtils.getAnnotationType(annotation)));
            }
        });
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws InvocationTargetException, IllegalAccessException {
        List<Annotation>[] lists = methodMap.get(ReflectUtils.findThisMethod(oldObject, method));
        if(lists != null){
            List<ValidationErrorInfo> validationErrorInfos = new ArrayList<>();
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < lists.length; i++) {
                ValidationErrorInfo validationErrorInfo = new ValidationErrorInfo();
                validationErrorInfo.setIndex(i);
                validationErrorInfo.setParameterName(parameters[i].getName());
                List<Annotation> list = lists[i];
                List<String> messages = new ArrayList<>();
                for (Annotation annotation : list) {

                    List<? extends ValidationRuleHeadler<? extends Annotation>> rule = ValidationRulePool.getRule(getClass(annotation));
                    for (ValidationRuleHeadler<? extends Annotation> validationRuleHeadler : rule) {
                        ValidationResult validationResult = (ValidationResult) METHOD.invoke(validationRuleHeadler, annotation, args[i]);
                        if (!validationResult.isOk()){
                            messages.add(validationResult.getMessage());
                        }
                    }
                }
                if(CollUtil.isNotEmpty(messages)){
                    validationErrorInfo.setMessage(messages);
                    validationErrorInfos.add(validationErrorInfo);
                }
            }
            if(CollUtil.isNotEmpty(validationErrorInfos)){
                throw new ValidationException("校验异常", method, validationErrorInfos);
            }
        }
        return method.invoke(oldObject, args);
    }

    private Class<Annotation> getClass(Annotation annotation){
        return annotationClassMap.get(annotation);
    }

}
