package org.zhq.esjob.config;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class JobEventConfig {
    private final DataSource dataSource;
    @Autowired
    public JobEventConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public JobEventConfiguration jobEventConfiguration(){
        return new JobEventRdbConfiguration(dataSource);
    }
}
