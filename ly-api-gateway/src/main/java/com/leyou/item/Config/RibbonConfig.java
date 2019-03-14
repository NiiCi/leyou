package com.leyou.item.Config;

import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;

@Configuration
public class RibbonConfig {
    @Bean
    public IClientConfig ribbonClientConfiguration(){
        DefaultClientConfigImpl clientConfig = new DefaultClientConfigImpl();
        clientConfig.set(CommonClientConfigKey.ConnectTimeout, 60000);
        clientConfig.set(CommonClientConfigKey.ReadTimeout, 60000);
        return clientConfig;
    }
}
