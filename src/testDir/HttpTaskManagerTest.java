package testDir;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.http.HttpTaskManager;
import ru.yandex.practicum.http.HttpTaskServer;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManagerTest {
    private static HttpTaskManager manager;
    private static HttpTaskServer server;

    @BeforeAll
    public static void setUp() throws IOException {
        server = new HttpTaskServer();
        server.start();
        manager = new HttpTaskManager(8080, false);
    }

    @Test
    public void testAddTasks() {
        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        tasks.add(task1);
        tasks.add(task2);

        manager.addTasks(tasks);

        Assertions.assertEquals(2, manager.getTaskList().size());
        Assertions.assertTrue(manager.getTaskList().contains(task1));
        Assertions.assertTrue(manager.getTaskList().contains(task2));
    }

    @Test
    public void testLoad() {
        manager.load();

        Assertions.assertFalse(manager.getTaskList().isEmpty());
        Assertions.assertFalse(manager.getEpicList().isEmpty());
        Assertions.assertFalse(manager.getSubtaskList().isEmpty());
    }

    @Test
    public void testSave() {
        Task task = new Task("Task", "Description");
        Epic epic = new Epic("Epic", "Description");
        Subtask subtask = new Subtask("Subtask", "Description", epic.getId());

        manager.generateTask(task);
        manager.generateEpic(epic);
        manager.generateSubtask(subtask);

        manager.save();

    }
}
