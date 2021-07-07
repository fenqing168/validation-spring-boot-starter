package cn.fenqing.spring.validation.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/7 9:28
 * @description 规则池
 */
public class ValidationRulePool {

    @AllArgsConstructor
    @Getter
    private static class Order{
        int order;
        ValidationRuleHeadler validationRuleHeadler;
    }

    private final static Map<Class<Annotation>, List<Order>> RULES = new ConcurrentHashMap<>();

    public static <T extends Annotation> List<ValidationRuleHeadler<T>> getRule(Class<T> annotationClass){
        List<Order> orders = RULES.get(annotationClass);
        return orders.stream().map(order -> (ValidationRuleHeadler<T>)order.getValidationRuleHeadler()).collect(Collectors.toList());
    }

    public static void registerRule(Class<Annotation> annotationClass, ValidationRuleHeadler<?> validationRuleHeadler, int order){
        List<Order> orders = RULES.computeIfAbsent(annotationClass, k -> new ArrayList<>());
        orders.add(new Order(order, validationRuleHeadler));
        orders.sort(Comparator.comparingInt(o -> o.order));
    }

}
