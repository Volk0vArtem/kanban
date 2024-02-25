package httpServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    String url;
    private String apiToken;
    HttpClient client;

    public String getUrl(){
        return url;
    }

    public KVTaskClient(String url) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        this.url = url;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "register"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        this.apiToken = response.body().toString();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                .POST(body)
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public String load(String key) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}
