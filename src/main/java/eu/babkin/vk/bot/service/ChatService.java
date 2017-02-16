package eu.babkin.vk.bot.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import eu.babkin.vk.bot.event.messages.IncomingMessagesListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatService implements IncomingMessagesListener {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private static final String MSG_PREFIX = "BOT -> ";

    @Value("${bot.chat.id}")
    private int chatId;

    @Autowired
    private VkApiClient vk;

    @Autowired
    private UserActor actor;

    public void sendMessage(String message) {
        try {
            vk.messages().send(actor).chatId(chatId).message(MSG_PREFIX + message).execute();
        } catch (ApiException e) {
            logger.error("Failed to send message due to api error", e);
        } catch (ClientException e) {
            logger.error("Failed to send message due to client error", e);
        }
    }

    @Override
    public void onMessage(String msg) {

    }
}
