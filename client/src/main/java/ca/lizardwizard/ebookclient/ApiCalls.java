package ca.lizardwizard.ebookclient;

import ca.lizardwizard.ebookclient.objects.Book;
import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiCalls {

    public static Book[] getBooks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://127.0.0.1:5000/books")).build();
        HttpResponse<String> resp = httpClient.send(req,HttpResponse.BodyHandlers.ofString());
        Gson gson = new Gson();
        return gson.fromJson(resp.body(),Book[].class);
    }



}
