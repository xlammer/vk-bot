package eu.babkin.vk.bot.config;

import com.google.gson.Gson;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:secret.properties")
public class BotConfiguration {

    @Bean
    VkApiClient getClient(){
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient, new Gson());
    }
}
