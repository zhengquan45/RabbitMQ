package org.zhq.rabbit.task.autoconfigure;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zhq.rabbit.task.parser.ElasticJobConfigParser;


@Configuration
//1.引入配置项
@EnableConfigurationProperties(JobZkProperties.class)
@ConditionalOnProperty(prefix = "elastic.job.zk", name = {"namespace", "serverLists"},matchIfMissing = true)
@Slf4j
public class JobParserAutoConfiguration {


    //2.初始化 zk 注册中心
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter zookeeperRegistryCenter(JobZkProperties jobZkProperties) {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(jobZkProperties.getServerLists(), jobZkProperties.getNamespace());
        zookeeperConfiguration.setBaseSleepTimeMilliseconds(jobZkProperties.getBaseSleepTimeMilliseconds());
        zookeeperConfiguration.setMaxRetries(jobZkProperties.getMaxRetries());
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(jobZkProperties.getConnectionTimeoutMilliseconds());
        zookeeperConfiguration.setSessionTimeoutMilliseconds(jobZkProperties.getSessionTimeoutMilliseconds());
        zookeeperConfiguration.setMaxSleepTimeMilliseconds(jobZkProperties.getMaxSleepTimeMilliseconds());
        zookeeperConfiguration.setDigest(jobZkProperties.getDigest());
        log.info("zookeeper registry init success. address:{} namespace:{}",jobZkProperties.getServerLists(),jobZkProperties.getNamespace());
        return new ZookeeperRegistryCenter(zookeeperConfiguration);
    }

    @Bean
    public ElasticJobConfigParser elasticJobConfigParser(JobZkProperties jobZkProperties,ZookeeperRegistryCenter zookeeperRegistryCenter){
        return new ElasticJobConfigParser(jobZkProperties,zookeeperRegistryCenter);
    }
}
