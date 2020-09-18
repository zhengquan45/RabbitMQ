package org.zhq.esjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"org.zhq.esjob.**"})
public class JobApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobApplication.class,args);
    }
}
