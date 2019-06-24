package com.shyrei.s3bucket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.jdbc.config.annotation.EnableRdsInstance;

@SpringBootApplication
@EnableRdsInstance(dbInstanceIdentifier="${cloud.aws.rds.dbInstanceIdentifier}", password="${cloud.aws.rds.shyreiDB.password}")
public class S3bucketApp {


    public static void main(String[] args) {
        SpringApplication.run(S3bucketApp.class, args);
    }

}
