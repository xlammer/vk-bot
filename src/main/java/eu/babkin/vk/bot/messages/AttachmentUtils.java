package eu.babkin.vk.bot.messages;

import com.vk.api.sdk.objects.docs.Doc;
import com.vk.api.sdk.objects.photos.Photo;

class AttachmentUtils {

    static String toAttachment(Photo photo) {
        return String.format("photo%d_%d", photo.getOwnerId(), photo.getId());
    }

    static String toAttachment(Doc doc) {
        return String.format("doc%d_%d", doc.getOwnerId(), doc.getId());
    }
}
