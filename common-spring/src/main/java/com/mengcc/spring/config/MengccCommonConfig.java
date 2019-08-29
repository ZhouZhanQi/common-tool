package com.mengcc.spring.config;

import com.mengcc.core.utils.SpringContextUtils;
import com.mengcc.spring.handle.ApiExceptionHandle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouzq
 * @date 2019/8/29
 * @desc
 */
@Slf4j
@Configuration
public class MengccCommonConfig {


    @Bean
    @ConditionalOnMissingBean
    public SpringContextUtils springContextUtils() {
        log.debug(">> 配置Spring的ApplicationContext持有");
        return new SpringContextUtils();
    }


    @Bean
    @ConditionalOnMissingBean
    public ApiExceptionHandle apiExceptionHandle() {
        log.debug(">> 配置统一异常管理");
        return new ApiExceptionHandle();
    }

}
