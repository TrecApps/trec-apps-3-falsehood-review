package com.trecapps.falsehoods.falsehoodReview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.trecapps.falsehoods.falsehoodReview","com.trecapps.falsehoods.falsehoodSearch"})
public class Driver {

    public static void main(String[] args) {
        SpringApplication.run(Driver.class, args);
    }

}
