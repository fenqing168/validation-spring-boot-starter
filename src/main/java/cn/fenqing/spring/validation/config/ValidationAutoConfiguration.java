package cn.fenqing.spring.validation.config;

import cn.fenqing.validation.code.ValidationProxyProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fenqing
 * @version 0.0.1
 * @date 2021/7/6 16:40
 * @description 校验自动配置
 */
@Configuration
public class ValidationAutoConfiguration {

    @Bean
    public ValidationProxyProcessor validationProxyProcessor(){
        return new ValidationProxyProcessor();
    }

}
