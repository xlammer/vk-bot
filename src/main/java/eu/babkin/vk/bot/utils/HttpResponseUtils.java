package eu.babkin.vk.bot.utils;

import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HttpResponseUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponseUtils.class);

    private final Gson gson;

    @Autowired
    public HttpResponseUtils(Gson gson) {
        this.gson = gson;
    }

    public <T> T toObject(HttpResponse response, Class<T> type) {
        try {
            String jsonString = EntityUtils.toString(response.getEntity());
            logger.debug("DS: {}", jsonString);
            return gson.fromJson(jsonString, type);
        } catch (IOException e) {
            logger.error("unable to extract content string from response {}", response, e);
            throw new RuntimeException(e);
        }
    }
}
