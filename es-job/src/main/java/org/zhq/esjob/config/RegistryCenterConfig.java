package org.zhq.esjob.config;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnExpression("'${zookeeper.address}'.length() > 0")
public class RegistryCenterConfig {

    //注册中心
    @Bean(name = "registryCenter",initMethod = "init")
    public ZookeeperRegistryCenter registryCenter(@Value("${zookeeper.address}")final String address,
                                                  @Value("${zookeeper.namespace}")final String namespace,
                                                  @Value("${zookeeper.connectionTimeout}")final int connectionTimeout,
                                                  @Value("${zookeeper.sessionTimeout}")final int sessionTimeout,
                                                  @Value("${zookeeper.maxRetries}")final int maxRetries){
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(address,namespace);
        zookeeperConfiguration.setConnectionTimeoutMilliseconds(connectionTimeout);
        zookeeperConfiguration.setSessionTimeoutMilliseconds(sessionTimeout);
        zookeeperConfiguration.setMaxRetries(maxRetries);
        return new ZookeeperRegistryCenter(zookeeperConfiguration);
    }

}
