package eu.babkin.vk.bot.photo;

public class PhotoUploadException extends Exception {

    public PhotoUploadException(String message) {
        super(message);
    }

    public PhotoUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
