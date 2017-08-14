package com.bidanet.log;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by xuejike on 2017/8/9.
 */
@Configuration
@Import({LogAspect.class,LogFactory.class})
public class LogAutoConfig {


}
