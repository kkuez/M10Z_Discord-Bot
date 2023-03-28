package m10z.bot;

import m10z.twitch.TwitchProcessor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.util.Properties;

public class BotProcessor {

    private final JDA bot;
    private Properties properties;
    private TwitchProcessor twitchProcessor;

    public BotProcessor(Properties properties) {
        this.properties = properties;
        bot = prepareDiscordBot();
    }

    private JDA prepareDiscordBot() {
        JDABuilder builder = JDABuilder.createDefault(properties.getProperty("bot_token"));
        builder.setActivity(Activity.watching("community.wasted.de"));
        return builder.build();
    }

    public void setTwitchProcessor(TwitchProcessor twitchProcessor) {
        this.twitchProcessor = twitchProcessor;
    }
}
