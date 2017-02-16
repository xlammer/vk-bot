package eu.babkin.vk.bot.event;

import com.google.gson.Gson;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.messages.LongpollParams;
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
import java.nio.charset.Charset;
import java.util.Map;

@Service
public class VkEventListener {

    private static final Logger logger = LoggerFactory.getLogger(VkEventListener.class);

    private final HttpClient httpClient;
    private final RequestBuilder requestBuilder;
    private final TaskExecutor taskExecutor;

    private Integer ts;

    @Autowired
    public VkEventListener(TaskExecutor taskExecutor, VkApiClient vk, UserActor actor) throws ClientException, ApiException {
        this.taskExecutor = taskExecutor;
        LongpollParams params = vk.messages().getLongPollServer(actor).execute();

        requestBuilder = RequestBuilder.get("https://" + params.getServer())
                .addParameter("act", "a_check")
                .addParameter("key", params.getKey())
                .addParameter("wait", "25")
                .addParameter("mode", "2")
                .addParameter("version", "1");

        this.ts = params.getTs();
        this.httpClient = HttpClientBuilder.create().build();

        taskExecutor.execute(this::poll);
        logger.info("Event listener started");
    }

    public void start() {
        taskExecutor.execute(this::poll);
    }

    private void poll() {
        HttpUriRequest request = requestBuilder.addParameter("ts", ts.toString()).build();

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

        logger.debug("got json string {}", jsonString);
        Map result = new Gson().fromJson(jsonString, Map.class); // FIXME: add proper deserialization

        logger.info("update -> {}", result);
        ts = (Integer) result.get("ts");
        poll();
    }


}
