package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;
import java.util.Set;

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

    void deleteTaskByIdentify(int id);

    void deleteSubtaskByIdentify(int id);

    void deleteEpicByIdentify(int id);

    public List<Epic> getEpicList();

    public List<Subtask> getSubtaskList();

    public List<Task> getTaskList();

    int generateId();

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();
}
