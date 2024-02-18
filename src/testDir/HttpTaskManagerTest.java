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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static HttpTaskServer server;

    @BeforeEach
    public void setUp() throws IOException {
        server = new HttpTaskServer();
        server.start();
        super.taskManager = new HttpTaskManager(8080, false);
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddTasks() {
       HttpTaskManager manager = new HttpTaskManager(8080);
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
        HttpTaskManager manager = new HttpTaskManager(8080);
        manager.load();

        Assertions.assertFalse(manager.getTaskList().isEmpty());
        Assertions.assertFalse(manager.getEpicList().isEmpty());
        Assertions.assertFalse(manager.getSubtaskList().isEmpty());
    }

    @Test
    public void testSave() {
        HttpTaskManager manager = new HttpTaskManager(8080);
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
        HttpTaskManager manager = new HttpTaskManager(8080);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(manager.getTaskList(), httpTaskManager.getTaskList(),
                "Список задач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllEpicsAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8080);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(manager.getEpicList(), httpTaskManager.getEpicList(),
                "Список эпиков после выгрузки не совпадает");
    }

    @Test
    public void testGetAllSubtasksAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8080);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(manager.getSubtaskList(), httpTaskManager.getSubtaskList(),
                "Список подзадач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllPrioritizedAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8080);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(manager.getPrioritizedTasks(), httpTaskManager.getPrioritizedTasks(),
                "Список задач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllHistoryAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8080);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8080, true);
        httpTaskManager.load();

        assertEquals(manager.getHistory(), httpTaskManager.getHistory(),
                "Список истории после выгрузки не совпадает");
    }
}
