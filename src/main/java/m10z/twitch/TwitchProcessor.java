package m10z.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import m10z.bot.BotProcessor;

import java.util.Properties;

public class TwitchProcessor {

    private final TwitchClient twitchClient;
    private Properties properties;
    private BotProcessor botProcessor;

    public TwitchProcessor(Properties properties) {
        this.properties = properties;

        OAuth2Credential oAuth2Credential = new OAuth2Credential(properties.getProperty("twitch_id"), properties.getProperty("twitch_token"));
        twitchClient = TwitchClientBuilder.builder()
                .withDefaultAuthToken(oAuth2Credential)
                .withEnableHelix(true)
                .build();
    }

    public void setBotProcessor(BotProcessor botProcessor) {
        this.botProcessor = botProcessor;
    }
}
