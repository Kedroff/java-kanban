package testDir;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.HttpTaskServer;
import ru.yandex.practicum.manager.InMemoryTaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {

    private static HttpTaskServer server;
    private static InMemoryTaskManager manager;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testTaskPostEndpoint() throws IOException {
        URL postUrl = new URL("http://localhost:8080/tasks");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("task data".getBytes());
        assertEquals(201, postConnection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(postConnection.getInputStream()))) {
            String responseBody = reader.lines().collect(Collectors.joining(","));
            Task expectedTask = new Task("Task 1", "Description 1");
            manager.generateTask(expectedTask);
            Task actualTask = parseTaskFromJson(responseBody);
            assertEquals(expectedTask, actualTask);
        }
    }

    @Test
    public void testTaskGetEndpoint() throws IOException {
        Task expectedTask = new Task("Task 1", "Description 1");
        manager.generateTask(expectedTask);

        URL getUrl = new URL("http://localhost:8080/tasks");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getConnection.getInputStream()))) {
            String responseBody = reader.lines().collect(Collectors.joining());
            Task task = parseTaskFromJson(responseBody);

            assertEquals(expectedTask,task);
        }
    }

    @Test
    public void testTaskDeleteEndpoint() throws IOException {
        URL deleteUrl = new URL("http://localhost:8080/tasks");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());

        assertTrue(manager.getTaskList().isEmpty());
    }

    @Test
    public void testSubtaskPostEndpoint() throws IOException {
        URL postUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("subtask data".getBytes());
        assertEquals(201, postConnection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(postConnection.getInputStream()))) {
            String responseBody = reader.lines().collect(Collectors.joining(","));

            Epic epic = new Epic("Epic 1", "Description 1");
            epic.setStartTime(LocalDateTime.now());
            epic.setDuration(10);
            epic.setEndTime(LocalDateTime.now().plusMinutes(10));
            manager.generateEpic(epic);

            Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
            subtask1.setStartTime(LocalDateTime.now());
            subtask1.setDuration(10);
            manager.generateSubtask(subtask1);

            Subtask actualTask = (Subtask) parseTaskFromJson(responseBody);
            assertEquals(subtask1, actualTask);
        }
    }

    @Test
    public void testSubtaskGetEndpoint() throws IOException {
        Epic epic = new Epic("Epic 1", "Description 1");
        epic.setStartTime(LocalDateTime.now());
        epic.setDuration(10);
        epic.setEndTime(LocalDateTime.now().plusMinutes(10));
        manager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        subtask1.setStartTime(LocalDateTime.now());
        subtask1.setDuration(10);
        manager.generateSubtask(subtask1);

        URL getUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getConnection.getInputStream()))) {
            String responseBody = reader.lines().collect(Collectors.joining());
            Subtask subtask = (Subtask) parseTaskFromJson(responseBody);

            assertEquals(subtask1,subtask);
        }
    }

    @Test
    public void testSubtaskDeleteEndpoint() throws IOException {
        URL deleteUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());

        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    public void testEpicPostEndpoint() throws IOException {
        URL postUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("epic data".getBytes());
        assertEquals(201, postConnection.getResponseCode());
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(postConnection.getInputStream()))) {
            String responseBody = reader.lines().collect(Collectors.joining(","));
            Epic epic = new Epic("Epic 1", "Description 1");
            manager.generateEpic(epic);
            Epic actualTask = (Epic) parseTaskFromJson(responseBody);
            assertEquals(epic, actualTask);
        }
    }

    @Test
    public void testEpicGetEndpoint() throws IOException {
        Epic epic = new Epic("Epic 1", "Description 1");
        manager.generateEpic(epic);

        URL getUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getConnection.getInputStream()))) {
            String responseBody = reader.lines().collect(Collectors.joining());
            Epic epic1 = (Epic) parseTaskFromJson(responseBody);

            assertEquals(epic,epic1);
        }
    }

    @Test
    public void testEpicDeleteEndpoint() throws IOException {
        URL deleteUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());

        assertTrue(manager.getEpicList().isEmpty());
    }

    @Test
    public void testHistoryGetEndpoint() throws IOException {
        URL getUrl = new URL("http://localhost:8080/history");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());
    }

    @Test
    public void testHistoryDeleteEndpoint() throws IOException {
        URL deleteUrl = new URL("http://localhost:8080/history");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());

        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    public void testHistoryPostEndpoint() throws IOException {
        URL postUrl = new URL("http://localhost:8080/history");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("history data".getBytes());
        assertEquals(201, postConnection.getResponseCode());
    }

    public static Task parseTaskFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Task.class);
    }
}
