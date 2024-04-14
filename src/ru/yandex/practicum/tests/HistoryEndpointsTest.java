package ru.yandex.practicum.tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static java.time.Month.AUGUST;

public class HistoryEndpointsTest {
    private HttpTaskServer httpTaskServer;
    private HttpClient client;
    private static final String DEFAULT_HISTORY_URI = "http://localhost:8080/history";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
            .create();
    @BeforeEach
    public void startServer() throws IOException {
        httpTaskServer = new HttpTaskServer();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void endServer() {
        httpTaskServer.closeConnection();

    }

    @Test
    public void getEmptyHistoryListTest() throws IOException, InterruptedException {
        URI uri = URI.create(DEFAULT_HISTORY_URI);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Not right response");
    }

    @Test
    public void getHistoryListTest() throws IOException, InterruptedException {
        URI uri1 = URI.create("http://localhost:8080/epics");
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .header("Content-Type", "application/json")
                .uri(uri1)
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response1.statusCode(), "Not right response");

        URI uri2 = URI.create("http://localhost:8080/subtasks");
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", 1,
                LocalDateTime.of(2023, AUGUST,28,13, 0), 60);
        HttpRequest request2 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask11)))
                .header("Content-Type", "application/json")
                .uri(uri2)
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response2.statusCode(), "Not right response");


        URI uri3 = URI.create("http://localhost:8080/subtasks");
        HttpRequest request3 = HttpRequest.newBuilder()
                .GET()
                .uri(uri3)
                .build();
        HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response3.statusCode(), "Not right response");

        URI uri4 = URI.create(DEFAULT_HISTORY_URI);
        HttpRequest request4 = HttpRequest.newBuilder()
                .GET()
                .uri(uri4)
                .build();
        HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response4.statusCode(), "Not right response");
    }
}