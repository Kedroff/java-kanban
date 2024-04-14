package ru.yandex.practicum.managers.taskManager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.TaskModel;

import java.util.ArrayList;

public interface TaskManager {
    ArrayList<TaskModel> getAllTasks();

    void deleteAllTasks();

    TaskModel getTaskByID(int id);

    TaskModel addNewTask(TaskModel newTask);

    TaskModel updateTask(TaskModel newTask);

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

    ArrayList<Subtask> getAllEpicSubtasks(int epicID);
}
