package ru.yandex.practicum.test;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.tasks.TaskModel;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class HistoryManagerTest {
    @Test
    void testAdd() {
        TaskModel task1 = new TaskModel("Read book every day", "30 pages");
        Managers.getDefault().addNewTask(task1);
        TaskModel task2 = new TaskModel("jump every day", "30 iterations");
        Managers.getDefault().addNewTask(task2);
        Managers.getDefault().getTaskByID(task1.getID());
        Managers.getDefault().getTaskByID(task2.getID());
        Managers.getDefault().getTaskByID(task1.getID());
        ArrayList<TaskModel> tasksList = new ArrayList<>();
        tasksList.add(task1);
        tasksList.add(task2);
        assertEquals(tasksList, Managers.getDefaultHistory().getHistory(), "Добавление элемента в историю " +
                "запросов работает неправильно");
        Managers.getDefault().deleteAllTasks();
    }

    @Test
    void testRemove() {
        TaskModel task1 = new TaskModel("Read book every day", "30 pages");
        TaskModel task2 = new TaskModel("jump every day", "30 iterations");
        TaskModel task3 = new TaskModel("goTest", "all");
        Managers.getDefault().addNewTask(task1);
        Managers.getDefault().addNewTask(task2);
        Managers.getDefault().addNewTask(task3);
        Managers.getDefault().getTaskByID(task1.getID());
        Managers.getDefault().getTaskByID(task2.getID());
        Managers.getDefault().getTaskByID(task3.getID());
        ArrayList<TaskModel> tasksList = new ArrayList<>();
        tasksList.add(task3);
        tasksList.add(task1);
        Managers.getDefault().deleteTaskByID(task2.getID());
        assertEquals(tasksList, Managers.getDefaultHistory().getHistory(), "Элемент из середины не был удалён");

        Managers.getDefault().deleteTaskByID(task1.getID());
        tasksList.remove(task1);
        assertEquals(tasksList, Managers.getDefaultHistory().getHistory(), "Элемент из конца не был удалён");

        Managers.getDefault().addNewTask(task1);
        Managers.getDefault().getTaskByID(task1.getID());
        Managers.getDefault().deleteTaskByID(task1.getID());
        assertEquals(tasksList, Managers.getDefaultHistory().getHistory(), "Элемент из начала не был удалён");

        Managers.getDefault().deleteAllTasks();
        assertNull(Managers.getDefaultHistory().getHistory(), "Из истории не был +" +
                "удалён последний элемент");
    }

    @Test
    void testGetHistory() {
        assertNull(Managers.getDefaultHistory().getHistory(), "Возвращение не null при пустой истории");
        TaskModel task1 = new TaskModel("Read book every day", "30 pages");
        TaskModel task2 = new TaskModel("jump every day", "30 iterations");
        Managers.getDefault().addNewTask(task1);
        Managers.getDefault().addNewTask(task2);
        Managers.getDefault().getTaskByID(task1.getID());
        Managers.getDefault().getTaskByID(task2.getID());
        ArrayList<TaskModel> tasksList = new ArrayList<>();
        tasksList.add(task2);
        tasksList.add(task1);
        assertEquals(tasksList, Managers.getDefaultHistory().getHistory(), "Возвращена история в " +
                "неправильном порядке");
        Managers.getDefault().deleteAllTasks();
    }
}