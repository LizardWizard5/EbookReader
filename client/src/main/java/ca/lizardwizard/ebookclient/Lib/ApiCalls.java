package ca.lizardwizard.ebookclient.Lib;

import ca.lizardwizard.ebookclient.objects.Book;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiCalls {
    private static String baseUrl = "http://127.0.0.1:5000"; // Base URL for Python flask server
    /**
     * Pulls data from server
     * @return Book[] an array of books
     * @throws IOException
     * @throws InterruptedException
     */
    public static Book[] getBooks() throws IOException, InterruptedException {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/books")).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            return gson.fromJson(resp.body(), Book[].class);
        } catch (IOException e){
            throw new IOException("Error: API books request failed.",e);
        }

    }

    public static Book getBook(int id) throws IOException {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest req = HttpRequest.newBuilder().uri(URI.create(baseUrl + "/books/"+id)).build();
            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            Gson gson = new Gson();
            return gson.fromJson(resp.body(), Book.class);
        } catch (IOException e){
            throw new IOException("Error: API books request failed.",e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param bookId - Int specifying which book to pull audio for
     * @param ms - long Used to specify from which point more audio needs to be pulled
     * @return byte[] - Array of bytes that the server sends that represent a chunk of a wav file
     * @throws IOException
     */
    public static byte[] downloadAudioToMemory(int bookId,long ms) throws IOException {
        String urlStr = baseUrl + "/stream_audio/" + bookId+"/"+ms; // adjust host/port
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
