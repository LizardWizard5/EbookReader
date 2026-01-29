/**
 * AudioPlayer.java
 * Written by Eric Wooldridge
 * Purpose: Handle playing and overall controls of audio.
 */

package ca.lizardwizard.ebookclient.Lib;
import ca.lizardwizard.ebookclient.objects.Book;
import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Currently being rewritten to fit more with the pull more chunks as needed design the server is now pushing towards
 * This currently is being done with AudioInputStream and CLip but more research is needed to figure these out and maybe even a better method
 * Constructors:
 * AudioPlayer() - Only to be used for initializing an AudioPlayer, will not work without populating the currentBook var
 * AudioPlayer(Book)
 * Available Methods:
 * loadFromBytes(long) - Used to load more
 */
public class AudioPlayer {

    public InputStream getAudioStream(String bookId, int startMs) throws Exception {
        String urlString = String.format("http://your-server.com/stream/%s?t=%d", bookId, startMs);
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // We wrap it in a BufferedInputStream to prevent
        // constant small network requests (which causes lag)
        return new BufferedInputStream(connection.getInputStream(), 1024 * 32); // 32KB buffer
    }
   /* */


}
