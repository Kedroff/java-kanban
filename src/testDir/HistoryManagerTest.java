package testDir;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.HistoryManager;
import ru.yandex.practicum.manager.InMemoryHistoryManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void add() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }
    @Test
    public void dublicateAdd() {
        Task task = new Task("Task 1", "Description 1");

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void remove() {
        Task task = new Task("Task 1", "Description 1");
        historyManager.add(task);

        historyManager.remove(task.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void removeFromMiddle() {
        Task task1 = new Task("Task 1", "Description 1");
        historyManager.add(task1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(task1.getId()+1);
        historyManager.add(task2);
        Task task3 = new Task("Task 3", "Description 3");
        task3.setId(task2.getId()+1);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    public void testRemoveAllTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        Task task3 = new Task("Task 3", "Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.removeAllTasks();

        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void removeAllSubtasks() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 2);

        historyManager.add(subtask1);
        historyManager.add(subtask2);

        historyManager.removeAllSubtasks();

        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void removeAllEpics() {
        Epic epic1 = new Epic("Epic 1", "Epic Description 1");
        Epic epic2 = new Epic("Epic 2", "Epic Description 2");

        historyManager.add(epic1);
        historyManager.add(epic2);

        historyManager.removeAllEpics();

        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }
}