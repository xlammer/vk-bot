package eu.babkin.vk.bot.event;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.LongpollParams;
import eu.babkin.vk.bot.event.updates.UpdateListener;
import eu.babkin.vk.bot.messages.IncomingMessage;
import eu.babkin.vk.bot.utils.HttpResponseUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class PollingEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PollingEventListener.class);

    private Integer ts;

    private final URI basePollingUri;
    private final HttpClient httpClient;
    private final HttpResponseUtils responseUtils;
    private final TaskExecutor taskExecutor;
    private final List<UpdateListener<IncomingMessage>> messageListeners;

    @Autowired
    public PollingEventListener(TaskExecutor taskExecutor,
                                VkApiClient vk, UserActor actor,
                                HttpClient httpClient,
                                List<UpdateListener<IncomingMessage>> messageListeners,
                                HttpResponseUtils responseUtils) throws ClientException, ApiException {
        this.taskExecutor = taskExecutor;
        LongpollParams params = vk.messages().getLongPollServer(actor).execute();

        basePollingUri = RequestBuilder.get("https://" + params.getServer())
                .addParameter("act", "a_check")
                .addParameter("key", params.getKey())
                .addParameter("wait", "25")
                .addParameter("mode", "2")
                .addParameter("version", "1").build().getURI();

        this.ts = params.getTs();


        logger.info("Event listener started");
        this.httpClient = httpClient;
        this.messageListeners = messageListeners;
        this.responseUtils = responseUtils;
    }

    public void start() {
        while (true) {
            HttpUriRequest request = RequestBuilder.get(basePollingUri).addParameter("ts", ts.toString()).build();

            Event event;
            try {
                HttpResponse response = httpClient.execute(request);
                event = responseUtils.toObject(response, Event.class);
            } catch (Exception e) {
                logger.error("polling event failed", e);
                continue;
            }

            ts = event.getTs();

            event.getUpdates().forEach(update -> {
                if (update instanceof IncomingMessage) {
                    taskExecutor.execute(() -> notifyMessageListeners((IncomingMessage) update));
                }
            });
        }
    }

    private void notifyMessageListeners(IncomingMessage update) {
        messageListeners.forEach(listener -> listener.onUpdate(update));
    }

}
