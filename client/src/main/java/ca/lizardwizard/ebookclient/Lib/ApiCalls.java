package ca.lizardwizard.ebookclient.Lib;

import ca.lizardwizard.ebookclient.objects.Book;
import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
//TODO: For all methods, I want to add UI error feedback instead of just force closing.
public class ApiCalls {
    private static String baseUrl = "http://127.0.0.1:5000"; // Base URL for Python flask server
    /**
     * Pulls data from server
     * @return Book[] an array of books
     * @throws IOException
     * @throws InterruptedException
     */
    public static Book[] getBooks() throws IOException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            return httpClient.execute(new HttpGet(baseUrl + "/books"),
                    response -> {
                        if (response.getCode() != 200) {
                            throw new IOException("Error: API books request failed with code " + response.getCode());
                        }
                        String bodyAsString = EntityUtils.toString(response.getEntity());
                        if (bodyAsString == null || bodyAsString.isEmpty()) {
                            return new Book[]{new Book(-1, "No Books Found", "", "Maybe something is wrong with your connection?", "")};
                        }
                        Gson gson = new Gson();
                        return gson.fromJson(bodyAsString, Book[].class);
                    });
        }
    }

    public static Book getBook(int id) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        return httpClient.execute(new HttpGet(baseUrl+"/books/"+id),
                response->{
                    if(response.getCode()!=200){
                        throw new IOException("Error: API books request failed with code "+response.getCode());
                    }
                    String boduAsString = EntityUtils.toString(response.getEntity());
                    if(boduAsString==null || boduAsString.isEmpty()){
                        return new Book(-1, "No Books Found", "", "Maybe something is wrong with your connection?", "");
                    }
                    Gson gson = new Gson();
                    return gson.fromJson(boduAsString,Book.class);
                });

    }


    public static Boolean postBook(String title, String author, String desc, File cover, File pdf) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(baseUrl + "/upload");
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addTextBody("title", title, ContentType.TEXT_PLAIN)
                    .addTextBody("author", author, ContentType.TEXT_PLAIN)
                    .addTextBody("description", desc, ContentType.TEXT_PLAIN)
                    .addBinaryBody("cover", cover, ContentType.create("image/png"), cover.getName())
                    .addBinaryBody("pdf", pdf, ContentType.APPLICATION_OCTET_STREAM, pdf.getName())
                    .build();
            post.setEntity(entity);
            try (CloseableHttpResponse res = client.execute(post)) {
                if (res.getCode() != 200) {
                    throw new IOException("Error: API books request failed with code " + res.getCode());
                }
            }
        }
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
