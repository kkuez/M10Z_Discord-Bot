package m10z;

import m10z.bot.BotProcessor;
import m10z.twitch.TwitchProcessor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class LaunchHelper {
    private static Properties properties;

    public static void main(String[] args) throws IOException {
        properties = new Properties();
        properties.load(new FileInputStream("setup.properties"));

        BotProcessor botProcessor = new BotProcessor(properties);
        TwitchProcessor twitchProcessor = new TwitchProcessor(properties);

        botProcessor.setTwitchProcessor(twitchProcessor);
        twitchProcessor.setBotProcessor(botProcessor);
    }


}
