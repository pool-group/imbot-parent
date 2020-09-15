package com.yunli.imbot.core.service.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
/**
 * com.yunli.imbot.core.service.config
 *
 * @author duanpeng
 * @version Id: SqlConfig.java, v 0.1 2020年08月27日 14:51 duanpeng Exp $
 */
@Data
public class SqlConfig {

    @Value("${druid.master.url}")
    private String url;
    @Value("${druid.master.username}")
    private String username;
    @Value("${druid.master.password}")
    private String password;
    @Value("${druid.master.driver-class-name}")
    private String driverClassName;
}
