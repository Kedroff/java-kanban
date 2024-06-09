package ru.kedroff.project.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.kedroff.project.managers.Managers;
import ru.kedroff.project.tasks.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.Month.MAY;

public class SameTimeOfTasksTest {
    @Test
    public void sameTimeOfTasksTest() {
        Task task1 = new Task("Read book every day", "30 pages",
                LocalDateTime.of(2024, MAY, 13, 13, 0), 1440);
        Task task2 = new Task("jump every day", "30 iterations",
                LocalDateTime.of(2024, MAY, 14, 13, 0), 60);
        Managers.getDefault().addNewTask(task1);
        Managers.getDefault().addNewTask(task2);
        Managers.getDefault().getAllTasks();

        ArrayList<Task> expectedList = new ArrayList<>();
        expectedList.add(task1);
        Assertions.assertEquals(expectedList, Managers.getDefault().getAllTasks(), "Добавлены значения с " +
                "повторяющимися датами");

        Managers.getDefault().deleteAllTasks();
    }
}