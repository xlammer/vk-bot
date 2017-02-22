package eu.babkin.vk.bot.media;

public class MediaUploadException extends Exception {

    public MediaUploadException(String message) {
        super(message);
    }

    public MediaUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
