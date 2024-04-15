package ru.yandex.practicum.http;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;

import java.io.IOException;
import java.net.InetSocketAddress;


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

