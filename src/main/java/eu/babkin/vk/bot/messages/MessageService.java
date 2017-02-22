package eu.babkin.vk.bot.messages;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.objects.docs.Doc;
import com.vk.api.sdk.objects.photos.Photo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static eu.babkin.vk.bot.messages.AttachmentUtils.toAttachment;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final VkApiClient vk;
    private final UserActor actor;

    @Autowired
    public MessageService(VkApiClient vk, UserActor actor) {
        this.vk = vk;
        this.actor = actor;
    }

    public void sendChatMessage(String message, int peerId) {
        try {
            vk.messages().send(actor).peerId(peerId).message(message).execute();
        } catch (Exception e) {
            logger.error("Failed to send message", e);
        }
    }

    public void sendChatPhoto(Photo photo, int peerId) {
        try {
            vk.messages().send(actor).peerId(peerId).attachment(toAttachment(photo)).execute();
        } catch (Exception e) {
            logger.error("failed to send chat photo", e);
        }
    }

    public void sendChatDoc(Doc photo, int peerId) {
        try {
            vk.messages().send(actor).peerId(peerId).attachment(toAttachment(photo)).execute();
        } catch (Exception e) {
            logger.error("failed to send chat doc", e);
        }
    }

    public void sendChatSticker(int stickerId, int peerId) {
        try {
            vk.messages().send(actor).peerId(peerId).stickerId(stickerId).execute();
        } catch (Exception e) {
            logger.error("Failed to send sticker", e);
        }
    }
}
