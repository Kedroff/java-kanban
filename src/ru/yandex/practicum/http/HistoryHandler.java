package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

class HistoryHandler implements HttpHandler {
    InMemoryTasksManager inMemoryTasksManager;

    public HistoryHandler(InMemoryTasksManager inMemoryTasksManager) {
        this.inMemoryTasksManager = inMemoryTasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            Gson gson = new Gson();
            String response = gson.toJson(Managers.getDefaultHistory().getHistory());
            writeResponse(exchange, response, 200);
        } else {
            writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
    }
}
