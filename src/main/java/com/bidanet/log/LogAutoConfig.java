package com.bidanet.log;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by xuejike on 2017/8/9.
 */
@Configuration
@Import({LogAspect.class})
public class LogAutoConfig {

    @Bean
    public LogFactory logFactory(){
        LogFactory logFactory = new LogFactory();
        return logFactory;
    }

}
