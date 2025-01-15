package cn.odboy.infra.database;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Druid监控
 *
 * @author odboy
 * @date 2025-01-15
 */
@Configuration
public class DataSourceDruidMonitor {
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> registrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        // 允许访问的IP地址
        registrationBean.addInitParameter("allow", "0.0.0.0");
        // 拒绝访问的IP地址
        registrationBean.addInitParameter("deny", "");
        // 登录用户名
        registrationBean.addInitParameter("loginUsername", "admin");
        // 登录密码
        registrationBean.addInitParameter("loginPassword", "admin");
        // 是否允许重置统计数据
        registrationBean.addInitParameter("resetEnable", "false");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("druid.stat.mergeSql", "true");
        filterRegistrationBean.addInitParameter("druid.stat.slowSqlMillis", "1000");
        filterRegistrationBean.addInitParameter("druid.stat.logSlowSql", "true");
        return filterRegistrationBean;
    }
}
