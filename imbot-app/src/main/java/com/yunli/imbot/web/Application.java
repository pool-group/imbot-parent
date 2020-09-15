package com.yunli.imbot.web;

import com.yunli.imbot.common.util.IKAnalyzerUtil;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

/**
 * Hello world!
 *
 */
@ComponentScan(basePackages = {"com.yunli.imbot","com.assembly.template"})
@EntityScan(basePackages = {"com.yunli.imbot.common.dal.po"})
@EnableJpaRepositories(basePackages = {"com.yunli.imbot.common.dal.repository"})
@SpringBootApplication
public class Application
{
    @SneakyThrows
    public static void main(String[] args )
    {
        SpringApplication.run(Application.class, args);
    }
}
