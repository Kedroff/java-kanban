package ru.yandex.practicum.managers;

import ru.yandex.practicum.managers.historyManager.HistoryManager;
import ru.yandex.practicum.managers.historyManager.InMemoryHistoryManager;
import ru.yandex.practicum.managers.taskManager.InMemoryTasksManager;

public class Managers {
    private static final InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private static final InMemoryTasksManager taskManager = new InMemoryTasksManager();

    public static InMemoryTasksManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}