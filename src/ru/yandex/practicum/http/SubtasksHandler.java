package ru.yandex.practicum.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

class SubtasksHandler implements HttpHandler {
    InMemoryTasksManager inMemoryTasksManager;

    public SubtasksHandler(InMemoryTasksManager inMemoryTasksManager) {
        this.inMemoryTasksManager = inMemoryTasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getSubtaskOrSubtasks(exchange);
                break;
            case "POST":
                addOrUpdateSubtask(exchange);
                break;
            case "DELETE":
                deleteSubtaskOrSubtasks(exchange);
                break;
            default:
                writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }

    private void getSubtaskOrSubtasks(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();
        if (splitPath.length == 2) {
            writeResponse(exchange, gson.toJson(inMemoryTasksManager.getAllSubtasks()), 200);
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> subtaskIdOptional = getTaskId(exchange);
            if (subtaskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getSubtasksByID(subtaskIdOptional.get()) != null) {
                    writeResponse(exchange, gson.toJson(inMemoryTasksManager.getSubtasksByID(subtaskIdOptional.get())), 200);
                    return;
                }
            }
        }
        writeResponse(exchange, "Not found", 404);
    }

    private void addOrUpdateSubtask(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();

        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            writeResponse(exchange, "Not Acceptable", 406);
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Subtask subtaskFromJson = gson.fromJson(jsonObject, Subtask.class);
        if (splitPath.length == 2) {
            Subtask newSubtask = inMemoryTasksManager.addNewSubtask(subtaskFromJson);
            if (newSubtask == null) {
                writeResponse(exchange, "Not Acceptable", 406);
            } else {
                writeResponse(exchange, gson.toJson(newSubtask), 200);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> subtaskIdOptional = getTaskId(exchange);
            if (subtaskIdOptional.isPresent()) {
                if (subtaskIdOptional.get() != subtaskFromJson.getID()) {
                    writeResponse(exchange, "Not Acceptable", 406);
                    return;
                }
                if (inMemoryTasksManager.getSubtasksByID(subtaskIdOptional.get()) != null) {
                    Subtask updateSubtask = inMemoryTasksManager.updateSubtask(subtaskFromJson);
                    if (updateSubtask != null) {
                        writeResponse(exchange, gson.toJson(updateSubtask), 200);
                    } else {
                        writeResponse(exchange, "Not Acceptable", 406);
                        return;
                    }
                }
            }
        }
        writeResponse(exchange, "Not found", 404);
    }

    private void deleteSubtaskOrSubtasks(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        if (splitPath.length == 2) {
            inMemoryTasksManager.deleteAllSubtasks();
            try (OutputStream os = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(201, 0);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> subtaskIdOptional = getTaskId(exchange);
            if (subtaskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getSubtasksByID(subtaskIdOptional.get()) != null) {
                    inMemoryTasksManager.deleteSubtaskByID(subtaskIdOptional.get());
                    try (OutputStream os = exchange.getResponseBody()) {
                        exchange.sendResponseHeaders(201, 0);
                    }
                    return;
                }
            }
        }
        writeResponse(exchange, "Not found", 404);
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
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
