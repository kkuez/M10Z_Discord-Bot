package m10z.bot;

import com.github.twitch4j.events.ChannelGoLiveEvent;
import m10z.twitch.TwitchProcessor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        JDA buildBot = builder.build();
        try {
            buildBot.awaitReady();
        } catch (InterruptedException e) {
            //TODO Logging
            throw new RuntimeException(e);
        }
        return buildBot;
    }

    public void setTwitchProcessor(TwitchProcessor twitchProcessor) {
        this.twitchProcessor = twitchProcessor;
    }

    public void notifyLive(ChannelGoLiveEvent event) {
        String liveMessage = event.getChannel().getName() + " ist live!\nhttps://www.twitch.tv/" + event.getChannel().getName();
        System.out.println(liveMessage);

        Set<String> discordPostChannelNames = Arrays.stream(properties.getProperty("discord_post_channel").split(",")).collect(Collectors.toSet());
        List<TextChannel> textChannels = bot.getTextChannels().stream().filter(chan -> discordPostChannelNames.contains(chan.getName())).collect(Collectors.toList());

        textChannels.forEach(chan -> chan.sendMessage(liveMessage).queue());
    }
}
