package com.trecapps.falsehoods.falsehoodReview.repos;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "resourcesEntityManagerFactory",
        transactionManagerRef = "resourcesTransactionManager",
        basePackages = {"com.trecapps.falsehoods.falsehoodReview.repos"})

public class ResourceRepoConfig {

    @Bean(name = "resourcesDataSourceProperties")
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties primaryDataSourceProperties()
    {
        return new DataSourceProperties();
    }


    @Bean(name = "resourcesDataSource")
    @ConfigurationProperties("spring.datasource.configuration")
    public DataSource primaryDataSource(@Qualifier("resourcesDataSourceProperties") DataSourceProperties primaryDataSourceProperties) {
        DataSource ds = primaryDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        return ds;
    }


    @Bean(name = "resourcesEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean primaryEntityManagerFactory(
            EntityManagerFactoryBuilder primaryEntityManagerFactoryBuilder,
            @Qualifier("resourcesDataSource") DataSource primaryDataSource) {

        Map<String, String> primaryJpaProperties = new HashMap<>();
        primaryJpaProperties.put("hibernate.dialect", System.getenv("DB_DIALECT"));
        primaryJpaProperties.put("hibernate.hbm2ddl.auto", "update");
        primaryJpaProperties.put("hibernate.enable_lazy_load_no_trans", "true");

        LocalContainerEntityManagerFactoryBean ret = primaryEntityManagerFactoryBuilder
                .dataSource(primaryDataSource)
                .packages("com.trecapps.falsehoods.falsehoodReview.models")
                .persistenceUnit("resourcesDataSource")
                .properties(primaryJpaProperties)
                .build();

        return ret;
    }


    @Bean(name = "resourcesTransactionManager")
    public PlatformTransactionManager primaryTransactionManager(
            @Qualifier("resourcesEntityManagerFactory") EntityManagerFactory primaryEntityManagerFactory) {

        return new JpaTransactionManager(primaryEntityManagerFactory);
    }
}
