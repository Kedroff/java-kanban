package testDir;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.HttpTaskServer;
import ru.yandex.practicum.manager.InMemoryTaskManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private static HttpTaskServer server;

    @BeforeAll
    public static void setUp() throws IOException {
        server = new HttpTaskServer(new InMemoryTaskManager());
        server.start();
    }

    @AfterAll
    public static void tearDown() {
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
    }

    @Test
    public void testTaskGetEndpoint() throws IOException {
        URL getUrl = new URL("http://localhost:8080/tasks");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());
    }

    @Test
    public void testTaskDeleteEndpoint() throws IOException {
        URL deleteUrl = new URL("http://localhost:8080/tasks");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());
    }

    @Test
    public void testSubtaskPostEndpoint() throws IOException {
        URL postUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("subtask data".getBytes());
        assertEquals(201, postConnection.getResponseCode());
    }

    @Test
    public void testSubtaskGetEndpoint() throws IOException {
        URL getUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());
    }

    @Test
    public void testSubtaskDeleteEndpoint() throws IOException {
        URL deleteUrl = new URL("http://localhost:8080/subtasks");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());
    }

    @Test
    public void testEpicPostEndpoint() throws IOException {
        URL postUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection postConnection = (HttpURLConnection) postUrl.openConnection();
        postConnection.setRequestMethod("POST");
        postConnection.setDoOutput(true);
        postConnection.getOutputStream().write("epic data".getBytes());
        assertEquals(201, postConnection.getResponseCode());
    }

    @Test
    public void testEpicGetEndpoint() throws IOException {
        URL getUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection getConnection = (HttpURLConnection) getUrl.openConnection();
        getConnection.setRequestMethod("GET");
        assertEquals(200, getConnection.getResponseCode());
    }

    @Test
    public void testEpicDeleteEndpoint() throws IOException {
        URL deleteUrl = new URL("http://localhost:8080/epics");
        HttpURLConnection deleteConnection = (HttpURLConnection) deleteUrl.openConnection();
        deleteConnection.setRequestMethod("DELETE");
        assertEquals(204, deleteConnection.getResponseCode());
    }
}
