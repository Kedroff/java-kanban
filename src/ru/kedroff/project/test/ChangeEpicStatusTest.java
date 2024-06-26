package ru.kedroff.project.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.kedroff.project.managers.Managers;
import ru.kedroff.project.tasks.Epic;
import ru.kedroff.project.tasks.Subtask;
import ru.kedroff.project.tasks.TaskStatuses;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeEpicStatusTest {
    @Test
    public void emptyListOfSubtasks() {
        Epic epic = new Epic("посмотреть кино", "обязательно до конца месяца");
        Managers.getDefault().addNewEpic(epic);
        Assertions.assertEquals(TaskStatuses.NEW, epic.getStatus());
        Managers.getDefault().deleteAllEpics();
    }

    @Test
    public void listOfSubtasksWithStatusNew() {
        Epic epic = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic.getID(), TaskStatuses.NEW);
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic.getID(), TaskStatuses.NEW);
        Managers.getDefault().addNewSubtask(subtask12);
        assertEquals(TaskStatuses.NEW, epic.getStatus());
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    public void listOfSubtasksWithStatusDone() {
        Epic epic = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic.getID(), TaskStatuses.DONE);
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic.getID(), TaskStatuses.DONE);
        Managers.getDefault().addNewSubtask(subtask12);
        assertEquals(TaskStatuses.DONE, epic.getStatus());
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    public void listOfSubtasksWithStatusDoneAndNew() {
        Epic epic = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic.getID(), TaskStatuses.NEW);
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic.getID(), TaskStatuses.DONE);
        Managers.getDefault().addNewSubtask(subtask12);
        assertEquals(TaskStatuses.IN_PROGRESS, epic.getStatus());
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    public void listOfSubtasksWithStatusInProgress() {
        Epic epic = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic.getID(), TaskStatuses.IN_PROGRESS);
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic.getID(), TaskStatuses.IN_PROGRESS);
        Managers.getDefault().addNewSubtask(subtask12);
        assertEquals(TaskStatuses.IN_PROGRESS, epic.getStatus());
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }
}