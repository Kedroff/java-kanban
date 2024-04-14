package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

class PrioritizedHandler implements HttpHandler {
    InMemoryTasksManager inMemoryTasksManager;

    public PrioritizedHandler(InMemoryTasksManager inMemoryTasksManager) {
        this.inMemoryTasksManager = inMemoryTasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            Gson gson = new Gson();
            String response = gson.toJson(inMemoryTasksManager.getPrioritizedTasks());
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException("Error writing response", e);
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }


    private void writeResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Uncaught", e);
        }
    }


    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(splitPath[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
