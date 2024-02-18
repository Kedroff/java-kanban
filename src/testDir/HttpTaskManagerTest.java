package testDir;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.http.HttpTaskManager;
import ru.yandex.practicum.http.HttpTaskServer;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends HttpTaskManagerTestAbstract {
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

        assertEquals(2, manager.getTaskList().size());
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

    @Test
    public void testGetAllTasksAfterLoad() {
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(httpTaskManager.getTaskList(), httpTaskManager.getTaskList(),
                "Список задач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllEpicsAfterLoad() {
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(httpTaskManager.getEpicList(), httpTaskManager.getEpicList(),
                "Список эпиков после выгрузки не совпадает");
    }

    @Test
    public void testGetAllSubtasksAfterLoad() {
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(httpTaskManager.getSubtaskList(), httpTaskManager.getSubtaskList(),
                "Список подзадач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllPrioritizedAfterLoad() {
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(httpTaskManager.getPrioritizedTasks(), httpTaskManager.getPrioritizedTasks(),
                "Список задач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllHistoryAfterLoad() {
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(httpTaskManager.getHistory(), httpTaskManager.getHistory(),
                "Список истории после выгрузки не совпадает");
    }
}
