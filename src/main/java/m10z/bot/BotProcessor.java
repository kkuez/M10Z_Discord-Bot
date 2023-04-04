package m10z.bot;

import com.github.twitch4j.common.events.TwitchEvent;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import m10z.twitch.TwitchProcessor;
import m10z.youtube.YoutubeProcessor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class BotProcessor {

    private final JDA bot;
    private Properties properties;
    private TwitchProcessor twitchProcessor;
    private YoutubeProcessor youtubeProcessor;

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

    public void notifyTwitchLive(TwitchEvent event) {
        String eventClassString = event.getClass().toString();

        if(!event.getClass().equals(ChannelGoLiveEvent.class) && !event.getClass().equals(ChannelGoOfflineEvent.class)) {
            //TODO Logging
            throw new RuntimeException("Could not find event class implementation");
        }

        String twitchChannelName;
        String liveMessage = "No event class found.";
        switch (eventClassString) {
            case "class com.github.twitch4j.events.ChannelGoLiveEvent":
                twitchChannelName = ((ChannelGoLiveEvent)event).getChannel().getName();
                liveMessage = ":movie_camera: :movie_camera: :movie_camera: **" + twitchChannelName + "** *ist live!* :movie_camera: :movie_camera: :movie_camera: \nhttps://www.twitch.tv/" + twitchChannelName;
                break;
            case "class com.github.twitch4j.events.ChannelGoOfflineEvent":
                twitchChannelName = ((ChannelGoOfflineEvent)event).getChannel().getName();
                liveMessage = ":octagonal_sign: :octagonal_sign: :octagonal_sign: **" + twitchChannelName + "** *ist nicht mehr live.*:octagonal_sign: :octagonal_sign: :octagonal_sign: ";
                break;
        }

        List<TextChannel> textChannels = getLiveDiscordChannels();

        // TODO proper logging
        System.out.println(liveMessage);
        final String finalLiveMessage4Lambda = liveMessage;
        textChannels.forEach(chan -> chan.sendMessage(finalLiveMessage4Lambda).queue());
    }

    @NotNull
    private List<TextChannel> getLiveDiscordChannels() {
        Set<String> discordPostChannelNames = Arrays.stream(properties.getProperty("discord_post_channel").split(",")).collect(Collectors.toSet());
        List<TextChannel> textChannels = bot.getTextChannels().stream().filter(chan -> discordPostChannelNames.contains(chan.getName())).collect(Collectors.toList());
        return textChannels;
    }

    public void setYoutubeProcessor(YoutubeProcessor youtubeProcessor) {
        this.youtubeProcessor = youtubeProcessor;
    }

    public void notifyYoutubeLive(String channelName, String youtubeChannelUrl) {
        String liveMessage = "**" + channelName + "** *ist live!*\n" + youtubeChannelUrl;

        List<TextChannel> liveDiscordChannels = getLiveDiscordChannels();
        liveDiscordChannels.forEach(chan -> chan.sendMessage(liveMessage).queue());
    }
}
