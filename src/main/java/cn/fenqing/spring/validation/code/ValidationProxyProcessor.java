package cn.fenqing.spring.validation.code;

import cn.fenqing.validation.annotation.ActivateValidation;
import cn.fenqing.validation.annotation.ValidationHandler;
import cn.fenqing.validation.utils.ReflectUtils;
import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/6 16:52
 * @description 校验处理器
 */
@Configuration
@Slf4j
public class ValidationProxyProcessor implements BeanPostProcessor {

    /**
     * postProcessAfterInitialization 当bean创建完毕后触发
     *
     * @param bean
     * @param beanName
     * @return java.lang.Object
     * @author fenqing
     * @date 2021/7/6 16:54
     * @description postProcessAfterInitialization 当bean创建完毕后触发
     * @version 0.0.1
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Object proxy = proxy(bean, beanName);
        if(proxy != null){
            return proxy;
        }
        registerHandler(bean, beanName);
        return bean;
    }

    private void registerHandler(Object bean, String beanName){
        if(bean instanceof ValidationRuleHeadler){
            ValidationHandler annotation = bean.getClass().getAnnotation(ValidationHandler.class);
            if (annotation != null){
                Type typeArgument = TypeUtil.getTypeArgument(bean.getClass(), 0);
                Class<Annotation> aClass = (Class<Annotation>) TypeUtil.getClass(typeArgument);
                ValidationRulePool.registerRule(aClass, (ValidationRuleHeadler<?>) bean, annotation.order());
            }
        }
    }

    private Object proxy(Object bean, String beanName){
        //获取该bean的方法
        Class<?> clazz = bean.getClass();
        Method[] hasAnnotationMethods = ReflectUtils.getHasAnnotationMethods(clazz, ActivateValidation.class);
        boolean clazzHasAccotation = ReflectUtils.classHasAnnotation(clazz, ActivateValidation.class);
        if(clazzHasAccotation){
            hasAnnotationMethods = Arrays
                    .stream(clazz.getMethods())
                    .filter(method -> !Modifier.isStatic(method.getModifiers()))
                    .toArray(Method[]::new);
        }
        if(hasAnnotationMethods.length > 0){
            Map<Method, List<Annotation>[]> methodMap = Arrays.stream(hasAnnotationMethods).collect(Collectors.toMap(Function.identity(), ReflectUtils::methodAnnotations));
            ValidationProxyHandler validationProxyHandler = new ValidationProxyHandler(bean, methodMap);
            return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), validationProxyHandler);
        }
        return null;
    }
}
