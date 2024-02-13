package testDir;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.http.HttpTaskServer;
import ru.yandex.practicum.manager.Managers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    private static HttpTaskServer server;

    @BeforeAll
    public static void setUp() throws IOException {
        Managers.getDefaultKVServer();

        server = new HttpTaskServer();
        server.start();
    }

    @AfterAll
    public static void tearDown() {
        server.stop();
    }

    @Test
    public void testTaskEndpoints() throws IOException {
        URL postUrl = new URL("http://localhost:8080/tasks");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("task data".getBytes());
        assertEquals(201, postConnection.getResponseCode());

        URL getUrl = new URL("http://localhost:8080/tasks");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());

        URL deleteUrl = new URL("http://localhost:8080/tasks");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());
    }

    @Test
    public void testSubtaskEndpoints() throws IOException {
        URL postUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("subtask data".getBytes());
        assertEquals(201, postConnection.getResponseCode());

        URL getUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());

        URL deleteUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());
    }

    @Test
    public void testEpicEndpoints() throws IOException {
        URL postUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("epic data".getBytes());
        assertEquals(201, postConnection.getResponseCode());

        URL getUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());

        URL deleteUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());
    }
}
