package ru.yandex.practicum.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.InMemoryTaskManager;
import ru.yandex.practicum.tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private InMemoryTaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void getPrioritizedTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setStartTime(LocalDateTime.now().plusMinutes(5));

        Task task2 = new Task("Task 2", "Description 2");
        task2.setStartTime(LocalDateTime.now().plusMinutes(10));

        taskManager.generateTask(task1);
        taskManager.generateTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(task2, prioritizedTasks.get(1));
        assertEquals(task1, prioritizedTasks.get(0));
    }
}