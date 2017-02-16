package eu.babkin.vk.bot;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import eu.babkin.vk.bot.event.VkEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BotStarter {

    public static void main(String[] args) throws ClientException, ApiException {
        ConfigurableApplicationContext context = SpringApplication.run(BotStarter.class, args);

        context.getBean(VkEventListener.class).start();
    }
}
