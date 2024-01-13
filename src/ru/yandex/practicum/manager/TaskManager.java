package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;
import java.util.Map;

public interface TaskManager {
    void deleteTask();

    void deleteSubtask();

    void deleteEpic();

    Task getTaskByIdentify(int id);

    Subtask getSubtaskByIdentify(int id);

    Epic getEpicByIdentify(int id);

    int generateTask(Task task);

    int generateEpic(Epic epic);

    Integer generateSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void updateEpicStatus(Epic epic);

    void updateTaskStatus(Task task, Status status);

    void updateSubtaskStatus(Subtask subtask, Status status);

    void deleteTaskByIdentify(int id);

    void deleteSubtaskByIdentify(int id);

    void deleteEpicByIdentify(int id);

    Map<Integer, Epic> getEpicHashMap();

    Map<Integer, Subtask> getSubtaskHashMap();

    Map<Integer, Task> getTaskHashMap();

    int generateId();

    List<Task> getHistory();
}
