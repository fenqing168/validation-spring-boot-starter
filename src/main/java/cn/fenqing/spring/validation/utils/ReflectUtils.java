package cn.fenqing.spring.validation.utils;

import cn.hutool.core.util.ReflectUtil;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/7 9:49
 * @description 反射工具类
 */
public class ReflectUtils {

    /**
     * 有注解的方法
     * @param clazz
     * @param annClass
     * @return
     */
    public static Method[] getHasAnnotationMethods(Class<?> clazz, Class<? extends Annotation> annClass) {
        Method[] methods = ReflectUtil.getPublicMethods(clazz);
        Map<String, List<Method>> nameMapping = Arrays.stream(methods).collect(Collectors.groupingBy(Method::getName));
        Set<Method> methodSet = new HashSet<>();
        Deque<Class> deque = new ArrayDeque<>();
        deque.push(clazz);
        while (!deque.isEmpty()) {
            Class claTemp = deque.pop();
            Method[] anInterfaceMethods = ReflectUtil.getMethods(claTemp, method -> method.isAnnotationPresent(annClass));
            for (Method method : anInterfaceMethods) {
                String name = method.getName();
                List<Method> methods1 = nameMapping.get(name);
                if (methods1 != null) {
                    for (Method methodItem : methods1) {
                        Class<?>[] parameterTypes = methodItem.getParameterTypes();
                        Class<?>[] parameterTypes1 = method.getParameterTypes();
                        boolean flag = true;
                        for (int i = 0; i < parameterTypes.length; i++) {
                            if (parameterTypes[i] != parameterTypes1[i]) {
                                flag = false;
                                break;
                            }
                        }
                        if (flag) {
                            methodSet.add(method);
                            break;
                        }
                    }
                }
            }
            Class superclass = claTemp.getSuperclass();
            if(superclass != null){
                deque.push(superclass);
            }
            Class[] interfaces = claTemp.getInterfaces();
            for (Class anInterface : interfaces) {
                deque.push(anInterface);
            }
        }
        return methodSet.toArray(new Method[0]);
    }

    /**
     * 类上是否有注解
     * @param clazz
     * @param annClass
     * @return
     */
    public static boolean classHasAnnotation(Class<?> clazz, Class<? extends Annotation> annClass){
        return clazz.isAnnotationPresent(annClass)
                || clazz.getSuperclass().isAnnotationPresent(annClass)
                || Arrays.stream(clazz.getInterfaces()).anyMatch(claitem -> claitem.isAnnotationPresent(annClass));
    }

    /**
     * 获取方法
     * @param method
     * @return
     */
    private static Method[] getMethodUp(Method method){
        List<Method> res = new ArrayList<>();
        Deque<Class> deque = new ArrayDeque<>();
        deque.push(method.getDeclaringClass());
        while (!deque.isEmpty()) {
            Class clazz = deque.pop();
            try {
                Method temp = clazz.getMethod(method.getName(), method.getParameterTypes());
                res.add(temp);
            } catch (NoSuchMethodException e) {
                //
            }
            Class superclass = clazz.getSuperclass();
            if(superclass != null){
                deque.push(superclass);
            }
            Class[] interfaces = clazz.getInterfaces();
            for (Class anInterface : interfaces) {
                deque.push(anInterface);
            }
        }
        return res.toArray(new Method[0]);
    }

    /**
     * 获取该方法每个参数的注解，以及父类，接口上等
     * @param method
     * @return
     */
    public static List<Annotation>[] methodAnnotations(Method method){
        //当前方法
        List<Annotation>[] res = new List[method.getParameters().length];
        Method[] methodUp = getMethodUp(method);
        for (Method method1 : methodUp) {
            Parameter[] parameters = method1.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                Annotation[] annotations = parameter.getAnnotations();
                List<Annotation> re = res[i];
                if(re == null){
                    re = new ArrayList<>();
                    res[i] = re;
                }
                if(annotations.length > 0){
                    re.addAll(Arrays.asList(annotations));
                }
            }
        }
        return res;
    }

    @SneakyThrows
    public static Method findThisMethod(Object obj, Method method){
        return obj.getClass().getMethod(method.getName(), method.getParameterTypes());
    }

    @SneakyThrows
    public static Class<Annotation> getAnnotationType(Annotation annotation) {
        Field h = annotation.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        Object hv = h.get(annotation);
        Field type = hv.getClass().getDeclaredField("type");
        type.setAccessible(true);
        return (Class<Annotation>) type.get(hv);
    }

}
