package eu.babkin.vk.bot.utils;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.Charset;

@Service
public class HttpResponseUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpResponseUtils.class);

    private final Gson gson;

    @Autowired
    public HttpResponseUtils(Gson gson) {
        this.gson = gson;
    }

    private static String contentToString(HttpResponse response) {
        try {
            return IOUtils.toString(response.getEntity().getContent(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            logger.error("unable to extract content string from response {}", response, e);
            throw new RuntimeException(e);
        }
    }

    public <T> T fromResponse(HttpResponse response, Class<T> type) {
        String jsonString = contentToString(response);
        return gson.fromJson(jsonString, type);
    }
}
