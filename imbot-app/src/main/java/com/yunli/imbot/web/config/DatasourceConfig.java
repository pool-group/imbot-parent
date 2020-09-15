package com.yunli.imbot.web.config;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * druid
 *
 * @author k.y
 * @version Id: DatasourceConfig.java, v 0.1 2018年11月06日 下午17:51 k.y Exp $
 */
@Configuration
@Slf4j
public class DatasourceConfig {

//    @Value("${druid.monitor.username}")
//    private String username;
//    @Value("${druid.monitor.password}")
//    private String password;
//    @Value("${druid.monitor.allow}")
//    private String allow;
//    @Value("${druid.monitor.deny}")
//    private String deny;
//    @Value("${druid.monitor.publicKey}")
//    private String publicKey;

    @Bean(name = "masterDataSource")
    @Primary
    @ConfigurationProperties(prefix = "druid.master")
    public DataSource masterDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "slaveDataSource")
    @ConfigurationProperties(prefix = "druid.slave")
    public DataSource slaveDataSource1() {
        return new DruidDataSource();
    }

//    /**
//     * druid 数据源状态监控
//     * @return
//     */
//    @Bean
//    public ServletRegistrationBean statViewServlet() throws Exception {
//        //创建servlet注册实体
//        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(),"/druid/*");
//        //设置ip白名单
//        servletRegistrationBean.addInitParameter("allow",allow);//IP白名单 (没有配置或者为空，则允许所有访问)
//        //设置ip黑名单，如果allow与deny共同存在时,deny优先于allow
//        servletRegistrationBean.addInitParameter("deny",deny);//IP黑名单 (存在共同时，deny优先于allow)
//        //设置控制台管理用户
//        servletRegistrationBean.addInitParameter("loginUsername",username);
//        servletRegistrationBean.addInitParameter("loginPassword", ConfigTools.decrypt(publicKey,password));
//        //是否可以重置数据
//        servletRegistrationBean.addInitParameter("resetEnable","false");
//        return servletRegistrationBean;
//    }
//
//    /**
//     * druid 过滤器
//     * @return
//     */
//    @Bean
//    public FilterRegistrationBean statFilter(){
//        //创建过滤器
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
//        //设置过滤器过滤路径
//        filterRegistrationBean.addUrlPatterns("/*");
//        //忽略过滤的形式
//        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        return filterRegistrationBean;
//    }
}
