package ru.yandex.practicum.tests;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.http.HttpTaskServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static java.time.Month.MARCH;

public class TaskEndpointsTest {
    private HttpTaskServer httpTaskServer;
    private HttpClient client;
    private static final String DEFAULT_TASKS_URI = "http://localhost:8080/tasks";
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
    public void getEmptyListTasksTest() throws IOException, InterruptedException {
        URI uri = URI.create(DEFAULT_TASKS_URI);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response.statusCode(), "Not right response");
    }

    @Test
    public void addTaskAndGetItTest() throws IOException, InterruptedException {
        URI uri1 = URI.create(DEFAULT_TASKS_URI);
        Task task1 = new Task("Read book every day", "30 pages",
                LocalDateTime.of(2024, MARCH,28,13, 0), 60);
        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .header("Content-Type", "application/json")
                .uri(uri1)
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response1.statusCode(), "Not right response");

        URI uri2 = URI.create(DEFAULT_TASKS_URI);
        HttpRequest request2 = HttpRequest.newBuilder()
                .GET()
                .uri(uri2)
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response2.statusCode(), "Not right response");
    }

    @Test
    public void DeleteTaskTest() throws IOException, InterruptedException {
        Task task1 = new Task("Read book every day", "30 pages",
                LocalDateTime.of(2024, MARCH,28,13, 0), 60);
        URI uri1 = URI.create(DEFAULT_TASKS_URI);
        HttpRequest request1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .header("Content-Type", "application/json")
                .uri(uri1)
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(200, response1.statusCode(), "Not right response");

        URI uri2 = URI.create(DEFAULT_TASKS_URI + "/1");
        HttpRequest request2 = HttpRequest.newBuilder()
                .DELETE()
                .header("Content-Type", "application/json")
                .uri(uri2)
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        Assertions.assertEquals(201, response2.statusCode(), "Not right response");
    }
}