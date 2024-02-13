package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.server.KVServer;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;
    private final KVServer kvserver;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
        kvserver = new KVServer();
    }

    public void start() {
        server.start();
        System.out.println("Запуск сервера на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановка сервера");
    }

    public static void main(String[] args) throws IOException {
        final HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    private void handler(HttpExchange h) {
        try {
            System.out.println("\n/tasks: " + h.getRequestURI());
            final String path = h.getRequestURI().getPath().substring(7);
            switch (path) {
                case "":
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/ Ждет GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                        return;
                    }
                    final String response = gson.toJson(taskManager.getPrioritizedTasks());
                    kvserver.sendText(h, response);
                    break;
                case "history":
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/history Ждет GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                        return;
                    }
                    final String response2 = gson.toJson(taskManager.getHistory());
                    kvserver.sendText(h, response2);
                    break;
                case "task":
                    handleTask(h);
                    break;
                case "subtask":
                    handleSubtask(h);
                    break;
                case "subtask/epic":
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/subtask/epic Ждет GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                        return;
                    }
                    final String query = h.getRequestURI().getQuery();
                    String idParam = query.substring(3);
                    final int id = Integer.parseInt(idParam);
                    final List<Subtask> subtasks = new ArrayList<>();
                    subtasks.add(taskManager.getSubtaskByIdentify(id));
                    final String response3 = gson.toJson(subtasks);
                    System.out.println("Получили подзадачи эпика id =" + id);
                    kvserver.sendText(h, response3);
                    break;
                case "epic":
                    handleEpic(h);
                    break;
                default:
                    System.out.println("Неизвестный запрос: " + h.getRequestURI());
                    h.sendResponseHeaders(404, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET": {
                if (query == null) {
                    final List<Task> tasks = taskManager.getTaskList();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получили все задачи");
                    kvserver.sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Task task = taskManager.getTaskByIdentify(id);
                final String response = gson.toJson(task);
                System.out.println("Получили задачу id =" + id);
                kvserver.sendText(h, response);
                break;
            }
            case "DELETE": {
                if (query == null) {
                    taskManager.deleteTask();
                    System.out.println("Удалили все задачи");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.deleteTaskByIdentify(id);
                System.out.println("Удалили задачу id=" + id);
                h.sendResponseHeaders(400, 0);
                break;
            }
            case "POST": {
                String json = kvserver.readText(h);
                if (json.isEmpty()) {
                    System.out.println("Body с задачей пустой. Указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Task task = gson.fromJson(json, Task.class);
                final int id = task.getId();
                if (id != 0) {
                    taskManager.updateTask(task);
                    System.out.println("Обновили задачу id" + id);
                    h.sendResponseHeaders(200, 0);
                } else {
                    taskManager.generateTask(task);
                    System.out.println("Создали задачу id=" + id);
                    final String response = gson.toJson(task);
                    kvserver.sendText(h, response);
                    break;
                }
            }
        }
    }

    private void handleSubtask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                if (query == null) {
                    final List<Subtask> subtasks = taskManager.getSubtaskList();
                    final String response = gson.toJson(subtasks);
                    kvserver.sendText(h, response);
                } else {
                    String idParam = query.substring(3);
                    final int id = Integer.parseInt(idParam);
                    final Subtask subtask = taskManager.getSubtaskByIdentify(id);
                    final String response = gson.toJson(subtask);
                    kvserver.sendText(h, response);
                }
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.deleteSubtask();
                    h.sendResponseHeaders(200, 0);
                } else {
                    String idParam = query.substring(3);
                    final int id = Integer.parseInt(idParam);
                    taskManager.deleteSubtaskByIdentify(id);
                    h.sendResponseHeaders(200, 0);
                }
                break;
            case "POST":
                String json = kvserver.readText(h);
                if (json.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Subtask subtask = gson.fromJson(json, Subtask.class);
                final int id = subtask.getId();
                if (id != 0) {
                    taskManager.updateSubtask(subtask);
                } else {
                    taskManager.generateSubtask(subtask);
                }
                h.sendResponseHeaders(200, 0);
                break;
            default:
                h.sendResponseHeaders(405, 0);
        }
    }

    private void handleEpic(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch (h.getRequestMethod()) {
            case "GET":
                if (query == null) {
                    final List<Epic> epics = taskManager.getEpicList();
                    final String response = gson.toJson(epics);
                    kvserver.sendText(h, response);
                } else {
                    String idParam = query.substring(3);
                    final int id = Integer.parseInt(idParam);
                    final Epic epic = taskManager.getEpicByIdentify(id);
                    final String response = gson.toJson(epic);
                    kvserver.sendText(h, response);
                }
                break;
            case "DELETE":
                if (query == null) {
                    taskManager.deleteEpic();
                    h.sendResponseHeaders(200, 0);
                } else {
                    String idParam = query.substring(3);
                    final int id = Integer.parseInt(idParam);
                    taskManager.deleteEpicByIdentify(id);
                    h.sendResponseHeaders(200, 0);
                }
                break;
            case "POST":
                String json = kvserver.readText(h);
                if (json.isEmpty()) {
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Epic epic = gson.fromJson(json, Epic.class);
                final int id = epic.getId();
                if (id != 0) {
                    taskManager.updateEpic(epic);
                } else {
                    taskManager.generateEpic(epic);
                }
                h.sendResponseHeaders(200, 0);
                break;
            default:
                h.sendResponseHeaders(405, 0);
        }
    }

}
