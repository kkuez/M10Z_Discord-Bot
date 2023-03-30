package m10z.youtube;

import m10z.bot.BotProcessor;
import okhttp3.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class YoutubeProcessor {
    private Properties properties;
    private File cacheIfOnlineFile = new File(".", "isOnline.cache");
    private BotProcessor botProcessor;


    public YoutubeProcessor(Properties properties) throws IOException {
        this.properties = properties;

        // Reset caching online status file
        if(cacheIfOnlineFile.exists()) {
            FileUtils.forceDelete(cacheIfOnlineFile);
        }


        startRequestLoop();
    }

    private void startRequestLoop() {
        Runnable youtubeLoop = () -> {
            OkHttpClient okHttpClient = new OkHttpClient();

            Request.Builder builder = new Request.Builder();
            String youtubeChannelUrl = "https://www.youtube.com/channel/" + properties.getProperty("youtube_channel_id") + "/live";

            while(properties.keySet().contains("youtube_channel_id") && properties.getProperty("youtube_channel_id") != null) {
                //builder.url("https://www.youtube.com/channel/UCWiUTY3qnjIyjeDjCZwaMGQ/live");
                builder.url(youtubeChannelUrl);
                Request request = builder.build();

                Call call = okHttpClient.newCall(request);
                try {
                    Response response = call.execute();
                    String bodyAsString = new String(response.body().bytes());

                    if (bodyAsString.contains("isLive") && !cacheIfOnlineFile.exists()) {
                        System.out.println("Is Live!");
                        cacheIfOnlineFile.createNewFile();
                        String channelName = getChannelName(bodyAsString);

                        botProcessor.notifyYoutubeLive(channelName, youtubeChannelUrl);
                    } else {
                        System.out.println("Is not Live!");
                        if(cacheIfOnlineFile.exists()) {
                            FileUtils.forceDelete(cacheIfOnlineFile);
                        }
                    }

                } catch (IOException e) {
                    // TODO logging
                    throw new RuntimeException(e);
                }

                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    // TODO Logging
                    throw new RuntimeException(e);
                }
            }
        };

        new Thread(youtubeLoop).start();
    }

    private String getChannelName(String bodyAsString) {
        String notFineSubString = bodyAsString.substring(bodyAsString.indexOf("pageOwnerDetails"), bodyAsString.indexOf("externalChannelId"));
        String channelName = notFineSubString.split("name\":\"")[1].replace("\"", "").replace(",", "");
        return channelName;
    }

    public void setBotProcessor(BotProcessor botProcessor) {

        this.botProcessor = botProcessor;
    }
}
