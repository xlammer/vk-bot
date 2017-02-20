package eu.babkin.vk.bot.plugins;

import com.tumblr.jumblr.JumblrClient;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.PhotoPost;
import com.tumblr.jumblr.types.Post;
import com.vk.api.sdk.objects.photos.Photo;
import eu.babkin.vk.bot.BotCommands;
import eu.babkin.vk.bot.messages.IncomingMessage;
import eu.babkin.vk.bot.event.updates.UpdateListener;
import eu.babkin.vk.bot.messages.MessageService;
import eu.babkin.vk.bot.photo.PhotoUploadException;
import eu.babkin.vk.bot.photo.PhotoUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class TumblrPlugin implements UpdateListener<IncomingMessage> {

    private static final Logger logger = LoggerFactory.getLogger(TumblrPlugin.class);

    private final JumblrClient client;
    private final String blogUrl;
    private final PhotoUploader photoUploader;
    private final MessageService messageService;

    @Autowired
    public TumblrPlugin(@Value("${tumblr.consumer.secret}") String consumerSecret,
                        @Value("${tumblr.consumer.key}") String consumerKey,
                        @Value("${tumblr.photo.source.blog}") String blogUrl,
                        PhotoUploader photoUploader, MessageService messageService) {

        this.blogUrl = blogUrl;
        this.client = new JumblrClient(consumerKey, consumerSecret);
        this.photoUploader = photoUploader;
        this.messageService = messageService;
    }

    private String getRandomPhotoUrl() {
        Blog blog = client.blogInfo(blogUrl);

        Map<String, Integer> options = new HashMap<>();
        options.put("limit", 1);
        options.put("offset", new Random().nextInt(blog.getPostCount()));

        List<Post> posts = blog.posts(options);

        Post post = posts.get(0);
        if (post instanceof PhotoPost) {
            PhotoPost photoPost = (PhotoPost) post;
            return photoPost.getPhotos().get(0).getOriginalSize().getUrl();
        }
        return getRandomPhotoUrl();
    }

    @Override
    public void onUpdate(IncomingMessage incomingMessage) {
        if (BotCommands.TUMBLR.equals(incomingMessage.getMessage())) {
            String photoUrl = getRandomPhotoUrl();
            try {
                Photo photo = photoUploader.uploadPhoto(photoUrl);
                messageService.sendChatPhoto(photo, incomingMessage.getPeerId());
            } catch (PhotoUploadException e) {
                logger.error("failed to upload photo", e);
            }
        }
    }
}
