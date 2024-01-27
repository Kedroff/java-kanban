package ru.yandex.practicum.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.FileBackedTasksManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Task;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> { // Вообще тесты не проходят, выдает ошибку, не понимаю :(
    private FileBackedTasksManager manager;
    private File file;

    @BeforeEach
    public void beforeEach() {
        file = new File("testFile.csv");
        manager = new FileBackedTasksManager(file);
    }

    @Test
    public void emptyTaskList() {

        manager.save();

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);

        assertTrue(loadedManager.getTaskList().isEmpty());
        assertTrue(loadedManager.getSubtaskList().isEmpty());
        assertTrue(loadedManager.getEpicList().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

    @Test
    public void epicWithoutSubtasks() {
        Epic epic = new Epic("Epic 1", "Epic Description 1");

        manager.generateEpic(epic);
        manager.save();

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(1, loadedManager.getEpicList().size());
        assertTrue(loadedManager.getSubtaskList().isEmpty());
        assertTrue(loadedManager.getTaskList().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }

    @Test
    public void emptyHistory() {

        Task task = new Task("Task 1", "Description 1");

        manager.generateTask(task);
        manager.save();

        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);

        assertEquals(1, loadedManager.getTaskList().size());
        assertTrue(loadedManager.getSubtaskList().isEmpty());
        assertTrue(loadedManager.getEpicList().isEmpty());
        assertTrue(loadedManager.getHistory().isEmpty());
    }
}