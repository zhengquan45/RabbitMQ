package org.zhq.rabbit.task.annotation;

import org.springframework.context.annotation.Import;
import org.zhq.rabbit.task.autoconfigure.JobParserAutoConfiguration;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JobParserAutoConfiguration.class)
public @interface EnableElasticJob {
}
