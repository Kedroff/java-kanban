package ru.yandex.practicum.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;
import ru.yandex.practicum.tasks.TaskModel;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

class TasksHandler implements HttpHandler {
    InMemoryTasksManager inMemoryTasksManager;

    public TasksHandler(InMemoryTasksManager inMemoryTasksManager) {
        this.inMemoryTasksManager = inMemoryTasksManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        switch (method) {
            case "GET":
                getTaskOrTasks(exchange);
                break;
            case "POST":
                addOrUpdateTask(exchange);
                break;
            case "DELETE":
                deleteTaskOrTasks(exchange);
                break;
            default:
                writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }

    private void getTaskOrTasks(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();
        if (splitPath.length == 2) {
            writeResponse(exchange, gson.toJson(inMemoryTasksManager.getAllTasks()), 200);
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> taskIdOptional = getTaskId(exchange);
            if (taskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getTaskByID(taskIdOptional.get()) != null) {
                    writeResponse(exchange, gson.toJson(inMemoryTasksManager.getTaskByID(taskIdOptional.get())), 200);
                    return;
                }
            }
        }
        writeResponse(exchange, "Not found", 404);
    }

    private void addOrUpdateTask(HttpExchange exchange) throws IOException {
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
        TaskModel taskFromJson = gson.fromJson(jsonObject, TaskModel.class);

        if (splitPath.length == 2) {
            TaskModel newTask = inMemoryTasksManager.addNewTask(taskFromJson);
            if (newTask == null) {
                writeResponse(exchange, "Not Acceptable", 406);
            } else {
                writeResponse(exchange, gson.toJson(newTask), 200);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> taskIdOptional = getTaskId(exchange);
            if (taskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getTaskByID(taskIdOptional.get()) != null) {
                    TaskModel updateTask = inMemoryTasksManager.updateTask(taskFromJson);
                    if (updateTask != null) {
                        writeResponse(exchange, gson.toJson(updateTask), 200);
                    } else {
                        writeResponse(exchange, "Not Acceptable", 406);
                        return;
                    }
                }
            }
        }
        writeResponse(exchange, "Not found", 404);
    }

    private void deleteTaskOrTasks(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        if (splitPath.length == 2) {
            inMemoryTasksManager.deleteAllTasks();
            try (OutputStream os = exchange.getResponseBody()) {
                exchange.sendResponseHeaders(201, 0);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> taskIdOptional = getTaskId(exchange);
            if (taskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getTaskByID(taskIdOptional.get()) != null) {
                    inMemoryTasksManager.deleteTaskByID(taskIdOptional.get());
                    try (OutputStream os = exchange.getResponseBody()) {
                        exchange.sendResponseHeaders(201, 0);
                    }
                    return;
                }
            }
        }
        writeResponse(exchange, "Not found", 404);
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(splitPath[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
    }
}
