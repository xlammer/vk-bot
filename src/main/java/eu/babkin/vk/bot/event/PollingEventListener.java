package eu.babkin.vk.bot.event;

import com.google.gson.Gson;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.LongpollParams;
import eu.babkin.vk.bot.event.updates.MessageUpdate;
import eu.babkin.vk.bot.event.updates.UpdateNotifier;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;

@Service
public class PollingEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PollingEventListener.class);

    private final HttpClient httpClient;
    private final TaskExecutor taskExecutor;
    private final URI basePollingUri;

    private Integer ts;

    @Autowired
    private Gson gson;

    @Autowired
    private UpdateNotifier<MessageUpdate> messageNotifier;

    @Autowired
    public PollingEventListener(TaskExecutor taskExecutor, VkApiClient vk, UserActor actor) throws ClientException, ApiException {
        this.taskExecutor = taskExecutor;
        LongpollParams params = vk.messages().getLongPollServer(actor).execute();

        basePollingUri = RequestBuilder.get("https://" + params.getServer())
                .addParameter("act", "a_check")
                .addParameter("key", params.getKey())
                .addParameter("wait", "25")
                .addParameter("mode", "2")
                .addParameter("version", "1").build().getURI();

        this.ts = params.getTs();
        this.httpClient = HttpClientBuilder.create().build();

        logger.info("Event listener started");
    }

    public void start() {
        taskExecutor.execute(this::poll);
    }

    private void poll() {
        logger.info("querying with ts={}", ts);
        HttpUriRequest request = RequestBuilder.get(basePollingUri).addParameter("ts", ts.toString()).build();

        HttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            logger.error("cannot get update data for request {}", request);
            return;
        }

        String jsonString;
        try {
            jsonString = IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            logger.error("cannot get string from response", e);
            return;
        }

        logger.debug("EVT_IN: {}", jsonString);
        Event event = gson.fromJson(jsonString, Event.class);

        ts = event.getTs();

        event.getUpdates().stream()
                .filter( update -> update instanceof MessageUpdate)
                .forEach( update -> messageNotifier.onUpdate((MessageUpdate) update));

        poll();
    }


}
