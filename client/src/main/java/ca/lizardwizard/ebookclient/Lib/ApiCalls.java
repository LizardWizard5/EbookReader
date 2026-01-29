package ca.lizardwizard.ebookclient.Lib;

import ca.lizardwizard.ebookclient.objects.Book;
import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        String bodyString = "";
        return httpClient.execute(new HttpGet(baseUrl+"/books"),
                response->{
                    if(response.getCode()!=200){
                        throw new IOException("Error: API books request failed with code "+response.getCode());
                    }
                    String boduAsString = EntityUtils.toString(response.getEntity());
                    if(boduAsString==null || boduAsString.isEmpty()){
                        return new Book[]{new Book(-1, "No Books Found", "", "Maybe something is wrong with your connection?", "")};
                    }
                    Gson gson = new Gson();
                    return gson.fromJson(boduAsString,Book[].class);
                });
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


    public static Boolean postBook(String name, String author, String desc, File cover, File audio) throws IOException, InterruptedException {

        return true;
    }

    /**
     *
     * @param bookId - Int specifying which book to pull audio for
     * @param ms - Long specifiying current ms position
     * @return byte[] - Array of bytes that the server sends that represent a chunk of a wav file
     * @throws IOException
     */
    public static byte[] downloadAudioToMemory(int bookId,long ms) throws IOException {
        String urlStr = baseUrl + "/stream_audio/" + bookId;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_PARTIAL) {
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
