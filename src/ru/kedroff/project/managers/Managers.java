package ru.kedroff.project.managers;

import ru.kedroff.project.managers.historyManager.HistoryManager;
import ru.kedroff.project.managers.historyManager.InMemoryHistoryManager;
import ru.kedroff.project.managers.taskManager.InMemoryTasksManager;

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