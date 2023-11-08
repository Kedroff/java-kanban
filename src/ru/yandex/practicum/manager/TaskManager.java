package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    public ArrayList<Task> printTask();

    public ArrayList<Subtask> printSubtask();

    public ArrayList<Epic> printEpic();

    public void deleteTask();

    public void deleteSubtask();

    public void deleteEpic();

    public Task getTaskByIdentify(int id);

    public Subtask getSubtaskByIdentify(int id);

    public Epic getEpicByIdentify(int id);

    public int generateTask(Task task);

    public int generateEpic(Epic epic);

    public Integer generateSubtask(Subtask subtask);

    public void updateTask(Task task);

    public void updateSubtask(Subtask subtask);

    public void updateEpic(Epic epic);

    public void updateEpicStatus(Epic epic);

    public void updateTaskStatus(Task task, String status);

    public void updateSubtaskStatus(Subtask subtask, String status);

    public void deleteTaskByIdentify(int id);

    public void deleteSubtaskByIdentify(int id);

    public void deleteEpicByIdentify(int id);

    public int generateId();
}
