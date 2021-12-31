package com.trecapps.falsehoods.falsehoodReview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan({"com.trecapps.falsehoods.falsehoodReview","com.trecapps.falsehoods.falsehoodSearch"})
@EnableJpaRepositories(basePackages = {"com.trecapps.base.FalsehoodModel.repos", "com.trecapps.base.InfoResource.repos"})
@EntityScan(basePackages = {"com.trecapps.base.FalsehoodModel.models", "com.trecapps.base.InfoResource.models"})
public class Driver {

    public static void main(String[] args) {
        SpringApplication.run(Driver.class, args);
    }

}
