package ca.lizardwizard.ebookclient.Lib;

import ca.lizardwizard.ebookclient.objects.Book;
import ca.lizardwizard.ebookclient.objects.RecentlyListened;
import com.google.gson.Gson;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCalls {

    /**
     * Pulls data from server
     * @return Book[] an array of books
     * @throws IOException
     * @throws InterruptedException
     */
    public static Book[] getBooks() throws IOException {
        String host = new EnvReader<String>().readVar("HOST");
        String port = new EnvReader<String>().readVar("PORT");
        String baseUrl = "http://" + host + ":" + port;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            return httpClient.execute(new HttpGet(baseUrl+"/books"),
                    response -> {
                        if (response.getCode() != 200) {
                            //throw new IOException("Error: API books request failed with code " + response.getCode());
                            new Popup("Error","Error "+response.getCode(),"An error occurred when getting books, If you are the administration, please check server status","Ok");
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
        String host = new EnvReader<String>().readVar("HOST");
        String port = new EnvReader<String>().readVar("PORT");
        String baseUrl = "http://" + host + ":" + port;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        return httpClient.execute(new HttpGet(baseUrl+":"+port+"/books/"+id),
                response->{
                    if(response.getCode()!=200){
                        new Popup("Error","Error "+response.getCode(),"An error occurred when pulling book data, If you are the administration, please check server status","Ok");
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
        String host = new EnvReader<String>().readVar("HOST");
        String port = new EnvReader<String>().readVar("PORT");
        String baseUrl = "http://" + host + ":" + port;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(baseUrl+ "/upload");
            HttpEntity entity;
            if(cover!=null) {
                entity = MultipartEntityBuilder.create()
                        .addTextBody("title", title, ContentType.TEXT_PLAIN)
                        .addTextBody("author", author, ContentType.TEXT_PLAIN)
                        .addTextBody("description", desc, ContentType.TEXT_PLAIN)
                        .addBinaryBody("cover", cover, ContentType.create("image/jpeg"), cover.getName())
                        .addBinaryBody("pdf", pdf, ContentType.create("application/pdf"), pdf.getName())
                        .build();
            }
            else{
                entity = MultipartEntityBuilder.create()
                        .addTextBody("title", title, ContentType.TEXT_PLAIN)
                        .addTextBody("author", author, ContentType.TEXT_PLAIN)
                        .addTextBody("description", desc, ContentType.TEXT_PLAIN)
                        .addBinaryBody("pdf", pdf, ContentType.create("application/pdf"), pdf.getName())
                        .build();
            }
            post.setEntity(entity);
            try (CloseableHttpResponse res = client.execute(post)) {
                if (res.getCode() != 200) {
                    // Read response body (if any) and include it in the error message so callers can see API error details
                    HttpEntity resEntity = res.getEntity();
                    String respBody = "";
                    if (resEntity != null) {
                        respBody = EntityUtils.toString(resEntity);
                    }
                    String msg = "Error: API books request failed with code " + res.getCode();
                    if (!respBody.isEmpty()) {
                        msg += " - " + respBody;
                    }
                    new Popup("Error!","Error uploading book", "An error occurred when trying to create your book please refer to the following message","Ok",msg);
                }
            } catch (ParseException e) {
                new Popup("Error!","Error uploading book", "An error occurred when trying to create your book please refer to the following message","Ok",e.getMessage());

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
        String host = new EnvReader<String>().readVar("HOST");
        String port = new EnvReader<String>().readVar("PORT");
        String baseUrl = "http://" + host + ":" + port;
        String urlStr = baseUrl+ "/stream_audio/" + bookId;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_PARTIAL) {

            new Popup("Error","Error "+responseCode,"An error occurred when pulling audio, If you are the administration, please check server status","Ok");
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


    /**
     * Pulls recently listened data from server associated to books that have been listened to.
     * @return RecentlyListened[] an array of recently listened data, this includes book id, last listened position, and last listened date.
     * @throws FileNotFoundException if env.txt is not found, this should never happen as the application checks for this on startup and
     */
    public static RecentlyListened[] getRecentlyListened() throws FileNotFoundException {
        String host = new EnvReader<String>().readVar("HOST");
        String port = new EnvReader<String>().readVar("PORT");
        String baseUrl = "http://" + host + ":" + port;
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            return httpClient.execute(new HttpGet(baseUrl+"/books/recently_listened"),
                    response -> {
                        if (response.getCode() != 200) {
                            new Popup("Error","Error "+response.getCode(),"An error occurred when pulling recently listened data, If you are the administration, please check server status","Ok");
                            //throw new IOException("Error: API books request failed with code " + response.getCode());
                            return new RecentlyListened[0];
                        }
                        String bodyAsString = EntityUtils.toString(response.getEntity());

                        Gson gson = new Gson();
                        return gson.fromJson(bodyAsString, RecentlyListened[].class);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean postRecentlyListened(int bookId, long ms) throws IOException {
        String host = new EnvReader<String>().readVar("HOST");
        String port = new EnvReader<String>().readVar("PORT");
        String baseUrl = "http://" + host + ":" + port;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(baseUrl+ "/books/"+bookId+"/update_recently_listened");
            HttpEntity entity = MultipartEntityBuilder.create()
                    .addTextBody("position", String.valueOf(ms), ContentType.TEXT_PLAIN)
                    .build();
            post.setEntity(entity);
            try (CloseableHttpResponse res = client.execute(post)) {
                if (res.getCode() != 200) {
                    // Read response body (if any) and include it in the error message so callers can see API error details
                    HttpEntity resEntity = res.getEntity();
                    String respBody = "";
                    if (resEntity != null) {
                        respBody = EntityUtils.toString(resEntity);
                    }
                    String msg = "Error: API recently listened POST request failed with code " + res.getCode();
                    if (!respBody.isEmpty()) {
                        msg += " - " + respBody;
                    }
                    new Popup("Error!","Error updating recently listened", "An error occurred when trying to update your recently listened data please refer to the following message","Ok",msg);
                }
            } catch (ParseException e) {
                new Popup("Error!","Error updating recently listened", "An error occurred when trying to update your recently listened data please refer to the following message","Ok",e.getMessage());

            }
        }
        return true;
    }


}
