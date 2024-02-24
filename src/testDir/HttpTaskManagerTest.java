package testDir;

import org.junit.jupiter.api.*;
import ru.yandex.practicum.http.HttpTaskManager;
import ru.yandex.practicum.server.KVServer;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private static KVServer server;

    @BeforeEach
    public void setUp() throws IOException {
        server = new KVServer();
        server.start();
        super.taskManager = new HttpTaskManager(8082,false);
    }

    @AfterEach
    public void tearDown() {
        server.stopServer();
    }

    @Test
    public void testAddTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        super.taskManager.generateTask(task1);
        super.taskManager.generateTask(task2);

        HttpTaskManager manager = new HttpTaskManager(8082,true);

        assertEquals(2, manager.getTaskList().size());
        Assertions.assertEquals(task1, manager.getTaskByIdentify(task1.getId()));
        Assertions.assertEquals(task2, manager.getTaskByIdentify(task2.getId()));
    }


    @Test
    public void testLoad() {
        HttpTaskManager manager = new HttpTaskManager(8082,true);

        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        super.taskManager.generateTask(task1);
        super.taskManager.generateTask(task2);

        manager.addTasks(List.of(task1, task2));
        manager.save();

        HttpTaskManager loadedManager = new HttpTaskManager(8082, true);

        loadedManager.load();

        assertEquals(2, loadedManager.getTaskList().size());
        Assertions.assertTrue(loadedManager.getTaskList().contains(task1));
        Assertions.assertTrue(loadedManager.getTaskList().contains(task2));
    }

    @Test
    public void testSave() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");

        super.taskManager.generateTask(task1);
        super.taskManager.generateTask(task2);

        super.taskManager.addTasks(List.of(task1, task2));

        super.taskManager.save();

        HttpTaskManager manager = new HttpTaskManager(8082, true);

        assertEquals(2, manager.getTaskList().size());

        Assertions.assertEquals(task1, manager.getTaskByIdentify(task1.getId()));
        Assertions.assertEquals(task2, manager.getTaskByIdentify(task2.getId()));
    }

    @Test
    public void testGetAllTasksAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8082);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8082, true);
        httpTaskManager.load();

        assertEquals(manager.getTaskList(), httpTaskManager.getTaskList(),
                "Список задач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllEpicsAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8082);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8082, true);
        httpTaskManager.load();

        assertEquals(manager.getEpicList(), httpTaskManager.getEpicList(),
                "Список эпиков после выгрузки не совпадает");
    }

    @Test
    public void testGetAllSubtasksAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8082);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8082, true);
        httpTaskManager.load();

        assertEquals(manager.getSubtaskList(), httpTaskManager.getSubtaskList(),
                "Список подзадач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllPrioritizedAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8082);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8082, true);
        httpTaskManager.load();

        assertEquals(manager.getPrioritizedTasks(), httpTaskManager.getPrioritizedTasks(),
                "Список задач после выгрузки не совпадает");
    }

    @Test
    public void testGetAllHistoryAfterLoad() {
        HttpTaskManager manager = new HttpTaskManager(8082);
        HttpTaskManager httpTaskManager = new HttpTaskManager(8082, true);
        httpTaskManager.load();

        assertEquals(manager.getHistory(), httpTaskManager.getHistory(),
                "Список истории после выгрузки не совпадает");
    }
}