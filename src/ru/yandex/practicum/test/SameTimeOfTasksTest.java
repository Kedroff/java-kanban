package ru.yandex.practicum.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.tasks.TaskModel;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.Month.MAY;

public class SameTimeOfTasksTest {
    @Test
    public void sameTimeOfTasksTest() {
        TaskModel task1 = new TaskModel("Read book every day", "30 pages",
                LocalDateTime.of(2024, MAY, 13, 13, 0), 1440);
        TaskModel task2 = new TaskModel("jump every day", "30 iterations",
                LocalDateTime.of(2024, MAY, 14, 13, 0), 60);
        Managers.getDefault().addNewTask(task1);
        Managers.getDefault().addNewTask(task2);
        Managers.getDefault().getAllTasks();

        ArrayList<TaskModel> expectedList = new ArrayList<>();
        expectedList.add(task1);
        Assertions.assertEquals(expectedList, Managers.getDefault().getAllTasks(), "Добавлены значения с " +
                "повторяющимися датами");

        Managers.getDefault().deleteAllTasks();
    }
}