package eu.babkin.vk.bot.messages;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.photos.Photo;
import eu.babkin.vk.bot.event.updates.MessageUpdate;
import eu.babkin.vk.bot.event.updates.UpdateNotifier;
import eu.babkin.vk.bot.photo.PhotoUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static eu.babkin.vk.bot.messages.AttachmentUtils.toAttachment;

@Service
public class ChatService implements UpdateNotifier<MessageUpdate> {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private static final String MSG_PREFIX = "BOT -> ";

    private final VkApiClient vk;
    private final UserActor actor;

    @Autowired
    private PhotoUploader photoUploader;

    @Autowired
    public ChatService(VkApiClient vk, UserActor actor) {
        this.vk = vk;
        this.actor = actor;
    }

    public void sendMessage(String message, int chatId) {
        try {
            vk.messages().send(actor).chatId(chatId).message(MSG_PREFIX + message).execute();
        } catch (ApiException e) {
            logger.error("Failed to send message due to api error", e);
        } catch (ClientException e) {
            logger.error("Failed to send message due to client error", e);
        }
    }

    @Override
    public void onUpdate(MessageUpdate messageUpdate) {
        if (messageUpdate.getMessage().startsWith("/bot")) {
            sendMessage("hello!", messageUpdate.getChatId());
        }

    }

    private void sendPhoto(Photo photo, int chatId) throws ClientException, ApiException {
        vk.messages().send(actor).chatId(chatId).attachment(toAttachment(photo)).execute();
    }
}
