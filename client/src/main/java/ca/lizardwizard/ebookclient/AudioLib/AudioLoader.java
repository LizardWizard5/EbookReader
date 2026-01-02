package ca.lizardwizard.ebookclient.AudioLib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioLoader {

    //Currently, this doesn't actually properly stream audio. It just takes the audio stream from the server waits until its all loaded in memory and then plays.
    public static byte[] downloadAudioToMemory(int bookId) throws IOException {
        String urlStr = "http://localhost:5000/stream_audio/" + bookId; // adjust host/port
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to download audio, HTTP code: " + responseCode);
        }

        try (InputStream in = conn.getInputStream();
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            byte[] tmp = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(tmp)) != -1) {
                buffer.write(tmp, 0, bytesRead);
            }

            return buffer.toByteArray();
        }
    }

}
