package testDir;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.TaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> { // не хотят работать тесты, я не понимаю почееееемууууууууу
     T taskManager;

    @Test
    public void deleteTask() {
        Task task1 = new Task("Task 1", "Description 1");

        taskManager.generateTask(task1);

        taskManager.deleteTask();

        List<Task> tasks = taskManager.getTaskList();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void deleteSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        taskManager.generateSubtask(subtask1);

        taskManager.deleteSubtask();

        List<Subtask> subtasks = taskManager.getSubtaskList();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void deleteEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        taskManager.generateSubtask(subtask1);

        taskManager.deleteEpic();

        List<Epic> epics = taskManager.getEpicList();
        assertTrue(epics.isEmpty());
    }

    @Test
    public void getTaskByIdentify() {
        Task task1 = new Task("Task 1", "Description 1");
        int taskId = taskManager.generateTask(task1);

        Task retrievedTask = taskManager.getTaskByIdentify(taskId);
        assertNotNull(retrievedTask);
        assertEquals(task1, retrievedTask);

        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void getSubtaskByIdentify() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        int subtaskId = taskManager.generateSubtask(subtask1);

        Subtask retrievedSubtask = taskManager.getSubtaskByIdentify(subtaskId);
        assertNotNull(retrievedSubtask);
        assertEquals(subtask1, retrievedSubtask);

        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask1, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void getEpicByIdentify() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.generateEpic(epic1);

        Epic retrievedEpic = taskManager.getEpicByIdentify(epicId);
        assertNotNull(retrievedEpic);
        assertEquals(epic1, retrievedEpic);

        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic1, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    public void generateTask() {
        Task task1 = new Task("Task 1", "Description 1");
        int taskId = taskManager.generateTask(task1);

        Task retrievedTask = taskManager.getTaskByIdentify(taskId);
        assertNotNull(retrievedTask);
        assertEquals(task1, retrievedTask);

        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks);
        assertEquals(1,tasks.size());
        assertEquals(task1,tasks.get(0));
    }

    @Test
    public void generateEpic() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.generateEpic(epic1);

        Epic retrievedEpic = taskManager.getEpicByIdentify(epicId);
        assertNotNull(retrievedEpic);
        assertEquals(epic1, retrievedEpic);

        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics);
        assertEquals(1,epics.size());
        assertEquals(epic1,epics.get(0));
    }

    @Test
    public void generateSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", epic.getId());
        Integer subtaskId = taskManager.generateSubtask(subtask1);

        assertNotNull(subtaskId);

        Subtask retrievedSubtask = taskManager.getSubtaskByIdentify(subtaskId);
        assertNotNull(retrievedSubtask);
        assertEquals(subtask1, retrievedSubtask);

        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks);
        assertEquals(1,subtasks.size());
        assertEquals(subtask1,subtasks.get(0));
    }

    @Test
    public void updateTask() {
        Task originalTask = new Task("Test Task", "Task description");
        int taskId = taskManager.generateTask(originalTask);

        Task updatedTask = new Task("Updated Task", "Updated description");
        updatedTask.setId(taskId);

        taskManager.updateTask(updatedTask);

        Task savedTask = taskManager.getTaskByIdentify(taskId);

        assertNotNull(savedTask);
        assertEquals(updatedTask, savedTask);

        final List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(originalTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    public void updateSubtask() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.generateEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        Integer subtaskId = taskManager.generateSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Updated Subtask", "Updated Subtask Description", epic.getId());
        updatedSubtask.setId(subtaskId);

        taskManager.updateSubtask(updatedSubtask);

        Subtask retrievedSubtask = taskManager.getSubtaskByIdentify(subtaskId);
        assertNotNull(retrievedSubtask);
        assertEquals(updatedSubtask, retrievedSubtask);

        final List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");

    }

    @Test
    public void updateEpic() {
        Epic epic = new Epic("Epic 1", "Description 1");
        int epicId = taskManager.generateEpic(epic);

        Epic updatedEpic = new Epic("Updated Epic", "Updated Epic Description");
        updatedEpic.setId(epicId);

        taskManager.updateEpic(updatedEpic);

        Epic retrievedEpic = taskManager.getEpicByIdentify(epicId);
        assertNotNull(retrievedEpic);
        assertEquals(updatedEpic, retrievedEpic);

        final List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

    }

    @Test
    public void deleteTaskByIdentify() {
        Task task = new Task("Task 1", "Description 1");
        int taskId = taskManager.generateTask(task);

        taskManager.deleteTaskByIdentify(taskId);

        assertNull(taskManager.getTaskByIdentify(taskId));

        final List<Task> tasks = taskManager.getTaskList();

        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void deleteSubtaskByIdentify() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.generateEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        int subtaskId = taskManager.generateSubtask(subtask);

        taskManager.deleteSubtaskByIdentify(subtaskId);

        assertNull(taskManager.getSubtaskByIdentify(subtaskId));

        final List<Subtask> subtasks = taskManager.getSubtaskList();

        assertEquals(0, subtasks.size(), "Неверное количество задач.");
        assertTrue(subtasks.isEmpty());

    }

    @Test
    public void deleteEpicByIdentify() {
        Epic epic = new Epic("Epic 1", "Description 1");
        taskManager.generateEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Description 1", epic.getId());
        taskManager.generateSubtask(subtask);

        int epicId = epic.getId();
        taskManager.deleteEpicByIdentify(epicId);

        assertNull(taskManager.getEpicByIdentify(epicId));
        assertNull(taskManager.getSubtaskByIdentify(subtask.getId()));

        final List<Epic> epics = taskManager.getEpicList();

        assertEquals(0, epics.size(), "Неверное количество задач.");
        assertTrue(epics.isEmpty());
    }

}



