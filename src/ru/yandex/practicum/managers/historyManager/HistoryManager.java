package ru.yandex.practicum.managers.historyManager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;

public interface HistoryManager {
    void add(Task task);

    public void remove(int id);

    ArrayList<Task> getHistory();
}