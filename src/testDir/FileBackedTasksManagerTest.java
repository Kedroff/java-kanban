package testDir;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.FileBackedTasksManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> { // Вообще тесты не проходят, выдает ошибку, не понимаю :(

    @Test
    public void emptyTaskList() {

        File file = new File("testFile.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);

        fileBackedManager.save();

        assertTrue(fileBackedManager.getTaskList().isEmpty());
        assertTrue(fileBackedManager.getSubtaskList().isEmpty());
        assertTrue(fileBackedManager.getEpicList().isEmpty());
        assertTrue(fileBackedManager.getHistory().isEmpty());

        FileBackedTasksManager fileBackedManager1 = FileBackedTasksManager.loadFromFile(file);

        System.out.println("Проверка задач: " + fileBackedManager.getTaskList().equals(fileBackedManager1.getTaskList()));
        System.out.println("Проверка подзадач: " + fileBackedManager.getSubtaskList().equals(fileBackedManager1.getSubtaskList()));
        System.out.println("Проверка эпиков: " + fileBackedManager.getEpicList().equals(fileBackedManager1.getEpicList()));
        System.out.println("Проверка истории: " + fileBackedManager.getHistory().equals(fileBackedManager1.getHistory()));

        System.out.println("Сравнение значения идентификатора последней добавленной задачи: " + (fileBackedManager.getGeneratedIds() == fileBackedManager1.getGeneratedIds()));

    }

    @Test
    public void epicWithoutSubtasks() {
        File file = new File("testFile.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);

        Epic epic = new Epic("Epic 1", "Epic Description 1");

        fileBackedManager.generateEpic(epic);
        fileBackedManager.save();

        FileBackedTasksManager fileBackedManager1 = FileBackedTasksManager.loadFromFile(file);

        assertEquals(1, fileBackedManager1.getEpicList().size());
        assertTrue(fileBackedManager1.getSubtaskList().isEmpty());
        assertTrue(fileBackedManager1.getTaskList().isEmpty());
        assertTrue(fileBackedManager1.getHistory().isEmpty());

        System.out.println("Проверка задач: " + fileBackedManager.getTaskList().equals(fileBackedManager1.getTaskList()));
        System.out.println("Проверка подзадач: " + fileBackedManager.getSubtaskList().equals(fileBackedManager1.getSubtaskList()));
        System.out.println("Проверка эпиков: " + fileBackedManager.getEpicList().equals(fileBackedManager1.getEpicList()));
        System.out.println("Проверка истории: " + fileBackedManager.getHistory().equals(fileBackedManager1.getHistory()));

        System.out.println("Сравнение значения идентификатора последней добавленной задачи: " + (fileBackedManager.getGeneratedIds() == fileBackedManager1.getGeneratedIds()));

    }

    @Test
    public void emptyHistory() {
        File file = new File("testFile.csv");
        FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);

        Task task = new Task("Task 1", "Description 1");

        fileBackedManager.generateTask(task);
        fileBackedManager.save();

        FileBackedTasksManager fileBackedManager1 = FileBackedTasksManager.loadFromFile(file);

        assertEquals(1, fileBackedManager1.getTaskList().size());
        assertTrue(fileBackedManager1.getSubtaskList().isEmpty());
        assertTrue(fileBackedManager1.getEpicList().isEmpty());
        assertTrue(fileBackedManager1.getHistory().isEmpty());

        System.out.println("Проверка задач: " + fileBackedManager.getTaskList().equals(fileBackedManager1.getTaskList()));
        System.out.println("Проверка подзадач: " + fileBackedManager.getSubtaskList().equals(fileBackedManager1.getSubtaskList()));
        System.out.println("Проверка эпиков: " + fileBackedManager.getEpicList().equals(fileBackedManager1.getEpicList()));
        System.out.println("Проверка истории: " + fileBackedManager.getHistory().equals(fileBackedManager1.getHistory()));

        System.out.println("Сравнение значения идентификатора последней добавленной задачи: " + (fileBackedManager.getGeneratedIds() == fileBackedManager1.getGeneratedIds()));

    }
}
