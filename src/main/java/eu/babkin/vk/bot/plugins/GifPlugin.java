package eu.babkin.vk.bot.plugins;

import at.mukprojects.giphy4j.Giphy;
import at.mukprojects.giphy4j.entity.giphy.GiphyData;
import at.mukprojects.giphy4j.exception.GiphyException;
import com.vk.api.sdk.objects.docs.Doc;
import eu.babkin.vk.bot.BotCommands;
import eu.babkin.vk.bot.event.updates.UpdateListener;
import eu.babkin.vk.bot.messages.IncomingMessage;
import eu.babkin.vk.bot.messages.MessageService;
import eu.babkin.vk.bot.media.MediaUploadException;
import eu.babkin.vk.bot.media.MediaUploader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class GifPlugin implements UpdateListener<IncomingMessage> {

    private static final Logger logger = LoggerFactory.getLogger(GifPlugin.class);

    private final Random random;
    private final Giphy giphy;
    private final MediaUploader mediaUploader;
    private final MessageService messageService;


    @Autowired
    public GifPlugin(@Value("${giphy.api.key}") String apiKey, MediaUploader mediaUploader, MessageService messageService) {
        this.giphy = new Giphy(apiKey);

        this.random = new Random();

        this.mediaUploader = mediaUploader;
        this.messageService = messageService;
    }

    @Override
    public void onUpdate(IncomingMessage incomingMessage) {
        if (!incomingMessage.getMessage().startsWith(BotCommands.GIF)) {
            return;
        }

        List<String> gifUrls = getGifUrls(incomingMessage);
        String gifUrl = gifUrls.get(random.nextInt(gifUrls.size()));

        try {
            Doc document = mediaUploader.uploadDocument(gifUrl);
            messageService.sendChatDoc(document, incomingMessage.getPeerId());
        } catch (MediaUploadException e) {
            logger.error("failed to upload gif image", e);
        }
    }

    private List<String> getGifUrls(IncomingMessage incomingMessage) {
        String[] queryWords = incomingMessage.getMessage().split(" ");
        try {
            return getGifUrls(queryWords);
        } catch (Exception e) {
            throw new RuntimeException("cannot get gif urls ", e);
        }
    }

    private List<String> getGifUrls(String[] queryWords) throws GiphyException {
        List<GiphyData> giphyData = getGiphyData(queryWords);

        return giphyData.stream()
                .filter(data -> "gif".equals(data.getType()))
                .map(e -> e.getImages().getDownsizedLarge().getUrl())
                .collect(Collectors.toList());
    }

    private List<GiphyData> getGiphyData(String[] queryWords) throws GiphyException {
        List<GiphyData> giphyData = Collections.emptyList();
        if (queryWords.length > 1) {
            String query = StringUtils.join(queryWords, " ", 1, queryWords.length);
            giphyData = giphy.search(query, 40, 0).getDataList();
        }
        return giphyData.isEmpty() ? giphy.trend().getDataList() : giphyData;
    }
}
