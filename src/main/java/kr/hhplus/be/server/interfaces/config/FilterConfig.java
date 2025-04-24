package kr.hhplus.be.server.interfaces.config;

import kr.hhplus.be.server.interfaces.Filter.LoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/coupons/*", "/products/*", "/users/*", "/orders/*", "/points/*", "/payments/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
