package eu.babkin.vk.bot.oauth;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.WebDriverRunner;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.UserAuthResponse;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import static com.codeborne.selenide.Selenide.*;

@Service
@PropertySource("classpath:secret.properties")
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

    @Value("${vk.user.email}")
    private String email;

    @Value("${vk.user.password}")
    private String password;

    private final VkApiClient vk;

    @Autowired
    public Authenticator(VkApiClient vk) {
        this.vk = vk;
        Configuration.browser = WebDriverRunner.CHROME;
    }

    private String getAccessToken() {
        open(getOAuthUrl());

        $(By.name("email")).val(email);
        $(By.name("pass")).val(password);
        $(By.id("install_allow")).click();
        $$(By.tagName("button")).stream().filter(e -> "Allow".equals(e.text())).findFirst().ifPresent(SelenideElement::click);

        String token = WebDriverRunner.url().replace(redirectUri + "#code=", "");
        WebDriverRunner.webdriverContainer.closeWebDriver();
        return token;
    }

    @Bean
    public UserActor getUserActor() throws ClientException, ApiException {
        String code = getAccessToken();
        UserAuthResponse authResponse = vk.oauth()
                .userAuthorizationCodeFlow(clientId, secret, redirectUri, code)
                .execute();

        return new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
    }

    private String getOAuthUrl() {
        return "https://oauth.vk.com/authorize?client_id=" + clientId + "&display=page&redirect_uri=" + redirectUri + "&scope=" + scope + "&response_type=code";
    }

}
