package ru.yandex.practicum.managers.taskManager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskByID(int id);

    Task addNewTask(Task newTask);

    Task updateTask(Task newTask);

    void deleteTaskByID(int id);

    ArrayList<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtasksByID(int id);

    Subtask addNewSubtask(Subtask newSubtask);

    Subtask updateSubtask(Subtask newSubtask);

    void deleteSubtaskByID(int id);

    ArrayList<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicByID(int id);

    Epic addNewEpic(Epic newEpic);

    Epic updateEpic(Epic newEpic);

    void deleteEpicByID(int id);

    ArrayList<Subtask> getAllEpicSubtasks (int epicID);
}
