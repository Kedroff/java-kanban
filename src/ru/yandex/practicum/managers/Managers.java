package ru.yandex.practicum.managers;

import ru.yandex.practicum.managers.historyManager.*;
import ru.yandex.practicum.managers.taskManager.*;

public class Managers {
    private static InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private static InMemoryTasksManager taskManager = new InMemoryTasksManager();

    public static InMemoryTasksManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}