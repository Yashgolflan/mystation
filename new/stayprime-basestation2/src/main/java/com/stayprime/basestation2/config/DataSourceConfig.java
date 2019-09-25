/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stayprime.basestation2.config;

import com.stayprime.storage.util.PersistenceUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 *
 * @author benjamin
 */
@Configuration
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceConfig extends HikariConfig {
    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Autowired
    PropertiesConfiguration config;

    @Bean
    @Primary
    public DataSource dataSource() {
        Properties props = PersistenceUtil.createDbPropertiesFromConfig(config);
        return siteDataSource(props);
    }

    DataSource siteDataSource(Properties properties) {
        String jdbcUrl = properties.getProperty(PersistenceUtil.javax_persistence_jdbc_url);
        log.info("Creating dataSource for db: " + jdbcUrl);
        long start = System.currentTimeMillis();

        HikariConfig config = new HikariConfig();
        copyState(config);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(properties.getProperty(PersistenceUtil.javax_persistence_jdbc_user));
        config.setPassword(properties.getProperty(PersistenceUtil.javax_persistence_jdbc_password));
        HikariDataSource dataSource = new HikariDataSource(config);
        log.debug("Created dataSource in {} ms", (System.currentTimeMillis() - start));
        return dataSource;
    }

}
