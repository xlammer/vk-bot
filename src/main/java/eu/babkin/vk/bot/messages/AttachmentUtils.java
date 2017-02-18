package eu.babkin.vk.bot.messages;

import com.vk.api.sdk.objects.photos.Photo;

public class AttachmentUtils {

    public static String toAttachment(Photo photo){
        return String.format("photo%d_%d", photo.getOwnerId(), photo.getId());
    }
}
