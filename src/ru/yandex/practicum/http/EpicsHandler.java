package ru.yandex.practicum.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

class EpicsHandler implements HttpHandler {
    InMemoryTasksManager inMemoryTasksManager;

    public EpicsHandler(InMemoryTasksManager inMemoryTasksManager) {
        this.inMemoryTasksManager = inMemoryTasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getEpicOrEpicsOrEpicSubtasks(exchange);
                break;
            case "POST":
                addOrUpdateEpic(exchange);
                break;
            case "DELETE":
                deleteEpicOrEpics(exchange);
                break;
            default:
                writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }

    public void getEpicOrEpicsOrEpicSubtasks(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();
        if (splitPath.length == 2) {
            writeResponse(exchange, gson.toJson(inMemoryTasksManager.getAllEpics()), 200);
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> epicIdOptional = getTaskId(exchange);
            if (epicIdOptional.isPresent()) {
                if (inMemoryTasksManager.getEpicByID(epicIdOptional.get()) != null) {
                    writeResponse(exchange, gson.toJson(inMemoryTasksManager.getEpicByID(epicIdOptional.get())), 200);
                    return;
                }
            }
        } else if (splitPath.length == 4) {
            if (!splitPath[3].equals("subtasks")) {
                writeResponse(exchange, "Not found", 406);
            }
            Optional<Integer> epicIdOptional = getTaskId(exchange);
            if (epicIdOptional.isPresent()) {
                if (inMemoryTasksManager.getEpicByID(epicIdOptional.get()) != null) {
                    writeResponse(exchange, gson.toJson(inMemoryTasksManager.getAllEpicSubtasks(epicIdOptional.get())), 200);
                    return;
                }
            }
        }
        writeResponse(exchange, "Not found", 404);
    }

    public void addOrUpdateEpic(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        if (!jsonElement.isJsonObject()) {
            writeResponse(exchange, "Not Acceptable", 406);
            System.out.println(1);
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epicFromJson = gson.fromJson(jsonObject, Epic.class);

        if (splitPath.length == 2) {
            Epic newEpic = inMemoryTasksManager.addNewEpic(epicFromJson);
            if (newEpic == null) {
                writeResponse(exchange, "Not Acceptable", 406);
            } else {
                writeResponse(exchange, gson.toJson(newEpic), 200);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> epicIdOptional = getTaskId(exchange);
            if (epicIdOptional.isPresent()) {
                if (inMemoryTasksManager.getEpicByID(epicIdOptional.get()) != null) {
                    Epic updateEpic = inMemoryTasksManager.updateEpic(epicFromJson);
                    if (updateEpic != null) {
                        writeResponse(exchange, gson.toJson(updateEpic), 200);
                    } else {
                        writeResponse(exchange, "Not Acceptable", 406);
                    }
                    return;
                }
            }
        }
        writeResponse(exchange, "Not found", 404);
    }

    public void deleteEpicOrEpics(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        if (splitPath.length == 2) {
            inMemoryTasksManager.deleteAllEpics();
            try (OutputStream os = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(201, 0);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> epicIdOptional = getTaskId(exchange);
            if (epicIdOptional.isPresent()) {
                if (inMemoryTasksManager.getEpicByID(epicIdOptional.get()) != null) {
                    inMemoryTasksManager.deleteEpicByID(epicIdOptional.get());
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
