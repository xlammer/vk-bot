package eu.babkin.vk.bot.oauth;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Authenticator {

    private static final Logger logger = LoggerFactory.getLogger(Authenticator.class);

    @Value("${bot.client.id}")
    private int clientId;

    @Value("${bot.client.scope}")
    private String scope;

    @Value("${bot.oauth.redirect.uri}")
    private String redirectUri;

    @Value("${bot.secret.key}")
    private String secret;

    private final VkApiClient vk;

    @Autowired
    public Authenticator(VkApiClient vk) {
        this.vk = vk;
    }

    public String getAccessToken(){
        WebDriver driver = new FirefoxDriver();
        driver.get(getOAuthUrl());
        while (!driver.getCurrentUrl().startsWith(redirectUri)) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {}
            logger.info("waiting for user to enter VK credentials and approve required permissions");
        }
        String code = driver.getCurrentUrl().replace(redirectUri + "#code=", "");
        driver.close();
        return code;
    }

    public UserActor getUserActor(String code) throws ClientException, ApiException {
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(clientId, secret, redirectUri, code)
                .execute();

        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }

    private String getOAuthUrl() {
        return "https://oauth.vk.com/authorize?client_id=" + clientId + "&display=page&redirect_uri=" + redirectUri + "&scope=" + scope + "&response_type=code";
    }

}
