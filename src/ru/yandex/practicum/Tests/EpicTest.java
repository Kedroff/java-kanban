package ru.yandex.practicum.Tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.manager.InMemoryTaskManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private static InMemoryTaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    public void EpicStatusEmptySubtaskList(){
        Epic epic = new Epic("Epic","Description");
        epic.setStartTime(LocalDateTime.now());
        taskManager.generateEpic(epic);

        assertEquals(Status.NEW,epic.getStatus(),"Статусы не совпадают");

    }
    @Test
    public void EpicStatusSubtasksStatusNew(){
        Epic epic = new Epic("Epic","Description");
        epic.setStartTime(LocalDateTime.now());
        taskManager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask","Description",epic.getId());
        subtask1.setStartTime(LocalDateTime.now().plusMinutes(10));
        taskManager.generateSubtask(subtask1);

        assertEquals(Status.NEW,epic.getStatus());
    }

    @Test
    public void EpicStatusSubtasksStatusDone(){
        Epic epic = new Epic("Epic","Description");
        epic.setStartTime(LocalDateTime.now());
        taskManager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask","Description",epic.getId());
        subtask1.setStatus(Status.DONE);
        subtask1.setStartTime(LocalDateTime.now());
        taskManager.generateSubtask(subtask1);

        assertEquals(Status.DONE,epic.getStatus());
    }

    @Test
    public void EpicStatusSubtasksStatusNewAndDone(){
        Epic epic = new Epic("Epic","Description");
        epic.setStartTime(LocalDateTime.now());
        taskManager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask","Description",epic.getId());
        subtask1.setStatus(Status.DONE);
        subtask1.setStartTime(LocalDateTime.now());
        taskManager.generateSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask","Description",epic.getId());
        subtask2.setStartTime(LocalDateTime.now());
        taskManager.generateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS,epic.getStatus());
    }

    @Test
    public void EpicStatusSubtasksStatusInProgress(){
        Epic epic = new Epic("Epic","Description");
        epic.setStartTime(LocalDateTime.now());
        taskManager.generateEpic(epic);

        Subtask subtask1 = new Subtask("Subtask","Description",epic.getId());
        subtask1.setStatus(Status.IN_PROGRESS);
        subtask1.setStartTime(LocalDateTime.now());
        taskManager.generateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS,epic.getStatus());
    }
}