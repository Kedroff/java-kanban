package ru.yandex.practicum;

import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Покормить кота", "Кормить влажным кормом");
        manager.generateTask(task1);

        Task task2 = new Task("Помыть посуду", "Помыть тарелки и ложки");
        manager.generateTask(task2);


        Epic epic1 = new Epic("Учеба", "Нужно учиться");
        int epicId1 = manager.generateEpic(epic1);


        Subtask subtask1 = new Subtask("Методы", "Выучить методы", epicId1);
        manager.generateSubtask(subtask1);

        Subtask subtask2 = new Subtask("Классы", "Выучить классы", epicId1);
        manager.generateSubtask(subtask2);

        Epic epic2 = new Epic("Дом", "Нужно убраться");
        int epicId2 = manager.generateEpic(epic2);

        Subtask subtask3 = new Subtask("Уборка", "Мытье полов", epicId2);
        manager.generateSubtask(subtask3);


        manager.getSubtaskByIdentify(5);// 1
        manager.getSubtaskByIdentify(5);
        manager.getEpicByIdentify(3);
        manager.getEpicByIdentify(3);
        manager.getEpicByIdentify(3);
        manager.getEpicByIdentify(3);
        manager.getEpicByIdentify(3);
        manager.getEpicByIdentify(3);
        manager.getEpicByIdentify(3);
        manager.getSubtaskByIdentify(5); // 10
        manager.getEpicByIdentify(3);




        List<Task> listTask = manager.getHistory();
        System.out.println(listTask);
    }
}