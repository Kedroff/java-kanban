package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                    .create();
            writeResponse(exchange, gson.toJson(inMemoryTasksManager.getPrioritizedTasks()), 200);
        } else {
            writeResponse(exchange, "Method not allowed", 405);
        }
        exchange.close();
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        System.out.println("Sending response with code " + responseCode + ": " + responseString);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(responseCode, 0);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
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
