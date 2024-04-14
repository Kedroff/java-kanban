package ru.yandex.practicum.managers.historyManager;

import ru.yandex.practicum.tasks.TaskModel;

import java.util.ArrayList;

public interface HistoryManager {
    void add(TaskModel task);

    void remove(int id);

    ArrayList<TaskModel> getHistory();
}