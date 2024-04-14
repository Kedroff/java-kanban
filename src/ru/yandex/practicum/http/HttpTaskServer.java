package ru.yandex.practicum.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    InMemoryTasksManager inMemoryTasksManager;
    HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        this.inMemoryTasksManager = new InMemoryTasksManager();
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(inMemoryTasksManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(inMemoryTasksManager));
        httpServer.createContext("/epics", new EpicsHandler(inMemoryTasksManager));
        httpServer.createContext("/history", new HistoryHandler(inMemoryTasksManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(inMemoryTasksManager));
        httpServer.start();
    }

    public void closeConnection() {
        httpServer.stop(1);
    }
}

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
                HandlerUtils.writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }

    private void getTaskOrTasks(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();
        if (splitPath.length == 2) {
            HandlerUtils.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getAllTasks()), 200);
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> taskIdOptional = HandlerUtils.getTaskId(exchange);
            if (taskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getTaskByID(taskIdOptional.get()) != null) {
                    HandlerUtils.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getTaskByID(taskIdOptional.get())), 200);
                    return;
                }
            }
        }
        HandlerUtils.writeResponse(exchange, "Not found", 404);
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
            HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Task taskFromJson = gson.fromJson(jsonObject, Task.class);

        if (splitPath.length == 2) {
            Task newTask = inMemoryTasksManager.addNewTask(taskFromJson);
            if (newTask == null) {
                HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
            } else {
                HandlerUtils.writeResponse(exchange, gson.toJson(newTask), 200);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> taskIdOptional = HandlerUtils.getTaskId(exchange);
            if (taskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getTaskByID(taskIdOptional.get()) != null) {
                    Task updateTask = inMemoryTasksManager.updateTask(taskFromJson);
                    if (updateTask != null) {
                        HandlerUtils.writeResponse(exchange, gson.toJson(updateTask), 200);
                    } else {
                        HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
                        return;
                    }
                }
            }
        }
        HandlerUtils.writeResponse(exchange, "Not found", 404);
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
            Optional<Integer> taskIdOptional = HandlerUtils.getTaskId(exchange);
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
        HandlerUtils.writeResponse(exchange, "Not found", 404);
    }
}

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
                HandlerUtils.writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }

    private void getSubtaskOrSubtasks(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();
        if (splitPath.length == 2) {
            HandlerUtils.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getAllSubtasks()), 200);
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> subtaskIdOptional = HandlerUtils.getTaskId(exchange);
            if (subtaskIdOptional.isPresent()) {
                if (inMemoryTasksManager.getSubtasksByID(subtaskIdOptional.get()) != null) {
                    HandlerUtils.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getSubtasksByID(subtaskIdOptional.get())), 200);
                    return;
                }
            }
        }
        HandlerUtils.writeResponse(exchange, "Not found", 404);
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
            HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Subtask subtaskFromJson = gson.fromJson(jsonObject, Subtask.class);
        if (splitPath.length == 2) {
            Subtask newSubtask = inMemoryTasksManager.addNewSubtask(subtaskFromJson);
            if (newSubtask == null) {
                HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
            } else {
                HandlerUtils.writeResponse(exchange, gson.toJson(newSubtask), 200);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> subtaskIdOptional = HandlerUtils.getTaskId(exchange);
            if (subtaskIdOptional.isPresent()) {
                if (subtaskIdOptional.get() != subtaskFromJson.getID()) {
                    HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
                    return;
                }
                if (inMemoryTasksManager.getSubtasksByID(subtaskIdOptional.get()) != null) {
                    Subtask updateSubtask = inMemoryTasksManager.updateSubtask(subtaskFromJson);
                    if (updateSubtask != null) {
                        HandlerUtils.writeResponse(exchange, gson.toJson(updateSubtask), 200);
                    } else {
                        HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
                        return;
                    }
                }
            }
        }
        HandlerUtils.writeResponse(exchange, "Not found", 404);
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
            Optional<Integer> subtaskIdOptional = HandlerUtils.getTaskId(exchange);
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
        HandlerUtils.writeResponse(exchange, "Not found", 404);
    }
}

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
                HandlerUtils.writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }

    public void getEpicOrEpicsOrEpicSubtasks(HttpExchange exchange) throws IOException {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();
        if (splitPath.length == 2) {
            HandlerUtils.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getAllEpics()), 200);
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> epicIdOptional = HandlerUtils.getTaskId(exchange);
            if (epicIdOptional.isPresent()) {
                if (inMemoryTasksManager.getEpicByID(epicIdOptional.get()) != null) {
                    HandlerUtils.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getEpicByID(epicIdOptional.get())), 200);
                    return;
                }
            }
        } else if (splitPath.length == 4) {
            if (!splitPath[3].equals("subtasks")) {
                HandlerUtils.writeResponse(exchange, "Not found", 406);
            }
            Optional<Integer> epicIdOptional = HandlerUtils.getTaskId(exchange);
            if (epicIdOptional.isPresent()) {
                if (inMemoryTasksManager.getEpicByID(epicIdOptional.get()) != null) {
                    HandlerUtils.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getAllEpicSubtasks(epicIdOptional.get())), 200);
                    return;
                }
            }

        }
        HandlerUtils.writeResponse(exchange, "Not found", 404);
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
            HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
            System.out.println(1);
            return;
        }
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Epic epicFromJson = gson.fromJson(jsonObject, Epic.class);

        if (splitPath.length == 2) {
            Epic newEpic = inMemoryTasksManager.addNewEpic(epicFromJson);
            if (newEpic == null) {
                HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
            } else {
                HandlerUtils.writeResponse(exchange, gson.toJson(newEpic), 200);
            }
            return;
        } else if (splitPath.length == 3) {
            Optional<Integer> epicIdOptional = HandlerUtils.getTaskId(exchange);
            if (epicIdOptional.isPresent()) {
                if (inMemoryTasksManager.getEpicByID(epicIdOptional.get()) != null) {
                    Epic updateEpic = inMemoryTasksManager.updateEpic(epicFromJson);
                    if (updateEpic != null) {
                        HandlerUtils.writeResponse(exchange, gson.toJson(updateEpic), 200);
                    } else {
                        HandlerUtils.writeResponse(exchange, "Not Acceptable", 406);
                    }
                    return;
                }
            }
        }
        HandlerUtils.writeResponse(exchange, "Not found", 404);
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
            Optional<Integer> epicIdOptional = HandlerUtils.getTaskId(exchange);
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
        HandlerUtils.writeResponse(exchange, "Not found", 404);
    }
}

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
            HandlerUtils.writeResponse(exchange, gson.toJson(Managers.getDefaultHistory().getHistory()), 200);
        } else {
            HandlerUtils.writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }

}

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
            HandlerUtils.writeResponse(exchange, gson.toJson(inMemoryTasksManager.getPrioritizedTasks()), 200);
        } else {
            HandlerUtils.writeResponse(exchange, "Method not found", 405);
        }
        exchange.close();
    }
}

class HandlerUtils {
    protected static void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(StandardCharsets.UTF_8));
        }
    }

    protected static Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] splitPath = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(splitPath[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}