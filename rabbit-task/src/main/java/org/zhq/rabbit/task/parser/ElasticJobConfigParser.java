package org.zhq.rabbit.task.parser;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.executor.handler.JobProperties;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.util.StringUtils;
import org.zhq.rabbit.task.annotation.ElasticJobConfig;
import org.zhq.rabbit.task.autoconfigure.JobZkProperties;
import org.zhq.rabbit.task.enums.ElasticJobTypeEnum;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ElasticJobConfigParser implements ApplicationListener<ApplicationReadyEvent> {

    private JobZkProperties jobZkProperties;
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    public ElasticJobConfigParser(JobZkProperties jobZkProperties, ZookeeperRegistryCenter zookeeperRegistryCenter) {
        this.jobZkProperties = jobZkProperties;
        this.zookeeperRegistryCenter = zookeeperRegistryCenter;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            ApplicationContext applicationContext = event.getApplicationContext();
            Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ElasticJobConfig.class);
            Iterator<Object> iterator = beans.values().iterator();
            while (iterator.hasNext()) {
                Object jobObject = iterator.next();
                Class<?> clazz = jobObject.getClass();
                if (clazz.getName().contains("$")) {
                    String clazzName = clazz.getName();
                    clazz = Class.forName(clazzName.substring(0, clazzName.indexOf("$")));
                }
                List<String> interfaceSimpleNames = Arrays.stream(clazz.getInterfaces()).map(Class::getSimpleName).collect(Collectors.toList());
                ElasticJobConfig annotation = clazz.getAnnotation(ElasticJobConfig.class);
                String jobClazz = clazz.getName();
                String jobName = jobZkProperties.getNamespace() + "." + annotation.jobName();
                String cron = annotation.cron();
                String shardingItemParameters = annotation.shardingItemParameters();
                String description = annotation.description();
                String jobParameter = annotation.jobParameter();
                String jobExceptionHandler = annotation.jobExceptionHandler();
                String executorServiceHandler = annotation.executorServiceHandler();
                String jobShardingStrategyClass = annotation.jobShardingStrategyClass();
                String eventTraceRdbDataSource = annotation.eventTraceRdbDataSource();
                String scriptCommandLine = annotation.scriptCommandLine();
                boolean failover = annotation.failover();
                boolean misfire = annotation.misfire();
                boolean overwrite = annotation.overwrite();
                boolean disabled = annotation.disabled();
                boolean monitorExecution = annotation.monitorExecution();
                boolean streamingProcess = annotation.streamingProcess();

                int shardingTotalCount = annotation.shardingTotalCount();
                int monitorPort = annotation.monitorPort();
                int maxTimeDiffSeconds = annotation.maxTimeDiffSeconds();
                int reconcileIntervalMinutes = annotation.reconcileIntervalMinutes();

                JobCoreConfiguration jobCoreConfiguration = getJobCoreConfiguration(jobName, cron,
                        shardingTotalCount, shardingItemParameters,
                        jobParameter, description,
                        failover, misfire,
                        jobExceptionHandler, executorServiceHandler);

                JobTypeConfiguration jobTypeConfiguration = null;
                if (interfaceSimpleNames.contains(ElasticJobTypeEnum.SIMPLE.getType())) {
                    jobTypeConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, jobClazz);
                }

                if (interfaceSimpleNames.contains(ElasticJobTypeEnum.DATAFLOW.getType())) {
                    jobTypeConfiguration = new DataflowJobConfiguration(jobCoreConfiguration, jobClazz, streamingProcess);
                }

                if (interfaceSimpleNames.contains(ElasticJobTypeEnum.SCRIPT.getType())) {
                    jobTypeConfiguration = new ScriptJobConfiguration(jobCoreConfiguration, scriptCommandLine);
                }

                LiteJobConfiguration jobConfiguration = LiteJobConfiguration.newBuilder(jobTypeConfiguration)
                        .overwrite(overwrite)
                        .disabled(disabled)
                        .monitorExecution(monitorExecution)
                        .monitorPort(monitorPort)
                        .maxTimeDiffSeconds(maxTimeDiffSeconds)
                        .jobShardingStrategyClass(jobShardingStrategyClass)
                        .reconcileIntervalMinutes(reconcileIntervalMinutes)
                        .build();

                BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
                factory.setInitMethodName("init");
                factory.setScope("prototype");
                if (!interfaceSimpleNames.contains(ElasticJobTypeEnum.SCRIPT.getType())) {
                    factory.addConstructorArgValue(jobObject);
                }
                factory.addConstructorArgValue(zookeeperRegistryCenter);
                factory.addConstructorArgValue(jobConfiguration);
                if (StringUtils.hasText(eventTraceRdbDataSource)) {
                    BeanDefinitionBuilder rdbFactory = BeanDefinitionBuilder.rootBeanDefinition(JobEventConfiguration.class);
                    rdbFactory.addConstructorArgValue(eventTraceRdbDataSource);
                    factory.addConstructorArgValue(rdbFactory.getBeanDefinition());
                }

                List<BeanDefinition> elasticJobListeners = getTargetElasticJobListeners(annotation);
                factory.addConstructorArgValue(elasticJobListeners);

                DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                String registerBeanName = annotation.jobName() + "SpringJobScheduler";
                defaultListableBeanFactory.registerBeanDefinition(registerBeanName, factory.getBeanDefinition());
                SpringJobScheduler springJobScheduler = (SpringJobScheduler) defaultListableBeanFactory.getBean(registerBeanName);
                springJobScheduler.init();
                log.info(jobName + " init and started");
            }
            log.info("found {} task", beans.values().size());
        } catch (Exception e) {
            log.error("elastic job exception,system exit.", e);
            System.exit(1);
        }
    }

    private List<BeanDefinition> getTargetElasticJobListeners(ElasticJobConfig annotation) {
        List<BeanDefinition> result = new ManagedList<>(2);
        String listener = annotation.listener();
        if (StringUtils.hasText(listener)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(ElasticJobListener.class);
            factory.setScope("prototype");
            result.add(factory.getBeanDefinition());
        }

        String distributedListener = annotation.distributedListener();
        long startedTimeoutMilliseconds = annotation.startedTimeoutMilliseconds();
        long completedTimeoutMilliseconds = annotation.completedTimeoutMilliseconds();

        if (StringUtils.hasText(distributedListener)) {
            BeanDefinitionBuilder factory = BeanDefinitionBuilder.rootBeanDefinition(AbstractDistributeOnceElasticJobListener.class);
            factory.addConstructorArgValue(startedTimeoutMilliseconds);
            factory.addConstructorArgValue(completedTimeoutMilliseconds);
            result.add(factory.getBeanDefinition());
        }
        return result;
    }

    private JobCoreConfiguration getJobCoreConfiguration(String jobName, String cron,
                                                         int shardingTotalCount, String shardingItemParameters,
                                                         String jobParameter, String description,
                                                         boolean failover, boolean misfire,
                                                         String jobExceptionHandler, String executorServiceHandler) {
        return JobCoreConfiguration.newBuilder(
                jobName, cron, shardingTotalCount
        ).shardingItemParameters(shardingItemParameters)
                .jobParameter(jobParameter)
                .description(description)
                .failover(failover)
                .misfire(misfire)
                .jobProperties(JobProperties.JobPropertiesEnum.JOB_EXCEPTION_HANDLER.getKey(), jobExceptionHandler)
                .jobProperties(JobProperties.JobPropertiesEnum.EXECUTOR_SERVICE_HANDLER.getKey(), executorServiceHandler)
                .build();
    }
}
