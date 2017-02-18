package eu.babkin.vk.bot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import eu.babkin.vk.bot.event.updates.Update;
import eu.babkin.vk.bot.event.updates.UpdateDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class BotConfiguration {

    @Bean
    Gson getGson(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Update.class, new UpdateDeserializer());

        return builder.create();
    }

    @Bean
    VkApiClient getClient(Gson gson) {
        TransportClient transportClient = HttpTransportClient.getInstance();
        return new VkApiClient(transportClient, gson);
    }

    @Bean
    TaskExecutor getTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);
        return taskExecutor;
    }
}
