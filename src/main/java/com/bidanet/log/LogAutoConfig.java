package com.bidanet.log;


import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * Created by xuejike on 2017/8/9.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Import({LogAspect.class})
public class LogAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public LogFactory logFactory(){
        LogFactory logFactory = new LogFactory();
        return logFactory;
    }

}
