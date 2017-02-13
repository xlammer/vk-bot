package eu.babkin.vk.bot.web;

import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import eu.babkin.vk.bot.oauth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class InitController {

    private static final Logger logger = LoggerFactory.getLogger(InitController.class);

    private final Authenticator authenticator;

    @Autowired
    public InitController(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    @RequestMapping("/")
    public void init() throws IOException, InterruptedException, ClientException, ApiException {
        String code = authenticator.getAccessToken();
        logger.debug("received access token {}", code);

        UserActor userActor = authenticator.getUserActor(code);
        logger.debug("user -> {}", userActor);
    }

}
