package org.zhq.esjob.config;

import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zhq.esjob.task.SpringDataflowJob;

@Configuration
public class DataflowJobConfig {
    private final ZookeeperRegistryCenter registryCenter;
    private final JobEventConfiguration jobEventConfiguration;

    @Autowired
    public DataflowJobConfig(ZookeeperRegistryCenter registryCenter, JobEventConfiguration jobEventConfiguration) {
        this.registryCenter = registryCenter;
        this.jobEventConfiguration = jobEventConfiguration;
    }

    @Bean
    public DataflowJob dataflowJob() {
        return new SpringDataflowJob();
    }

    @Bean(initMethod = "init")
    public JobScheduler dataflowJobScheduler(final DataflowJob dataflowJob,
                                             @Value("${dataflowJob.cron}") String cron,
                                             @Value("${dataflowJob.shardingTotalCount}") int shardingTotalCount,
                                             @Value("${dataflowJob.shardingItemParameters}") String shardingItemParameters) {
        return new SpringJobScheduler(dataflowJob,
                                      registryCenter,
                                      getLiteJobConfiguration(dataflowJob.getClass(),
                                                              cron,
                                                              shardingTotalCount,
                                                              shardingItemParameters),
                                      jobEventConfiguration);
    }

    private LiteJobConfiguration getLiteJobConfiguration(Class<? extends DataflowJob> dataflowClass, String cron, int shardingTotalCount, String shardingItemParameters) {
        JobCoreConfiguration jobCoreConfiguration = JobCoreConfiguration.newBuilder(dataflowClass.getName(),cron,shardingTotalCount)
                .shardingItemParameters(shardingItemParameters)
                .build();
        return LiteJobConfiguration.newBuilder(
                new DataflowJobConfiguration(jobCoreConfiguration,
                                             dataflowClass.getCanonicalName(),
                              true))
                .overwrite(true)
                .build();
    }


}
