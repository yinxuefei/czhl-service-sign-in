package com.gdczhl.saas.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

@Log4j2
@Configuration
public class ApplicationConfig {

    @Bean
    public ApplicationRunner runner(DataSource dataSource) {
        //预加载 dataSource
        return args -> {
            log.info("dataSource: {}", dataSource);
            Connection connection = dataSource.getConnection();
            log.info("connection: {}", connection);
        };
    }

}
