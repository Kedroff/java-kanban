package ru.yandex.practicum.tests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.managers.taskManager.FileBackedTasksManager;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskStatuses;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static java.time.Month.MAY;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    @Test
    void testGetAllTasks() {
        ArrayList<Task> taskList = new ArrayList<>();
        assertEquals(taskList, Managers.getDefault().getAllTasks(), "Пустые листы задач не совпадают");
        Task task1 = new Task("Read book every day", "30 pages");
        Managers.getDefault().addNewTask(task1);
        Task task2 = new Task("jump every day", "30 iterations");
        Managers.getDefault().addNewTask(task2);
        taskList.add(task1);
        taskList.add(task2);
        assertEquals(taskList, Managers.getDefault().getAllTasks(), "Листы задач не равны");
        Managers.getDefault().deleteAllTasks();
    }

    @Test
    void testDeleteAllTasks() {
        Task task1 = new Task("Read book every day", "30 pages");
        Managers.getDefault().addNewTask(task1);
        Task task2 = new Task("jump every day", "30 iterations");
        Managers.getDefault().addNewTask(task2);
        Managers.getDefault().deleteAllTasks();
        assertTrue(Managers.getDefault().getAllTasks().isEmpty(), "Задачи не были удалены");
        Managers.getDefault().deleteAllTasks();
        assertDoesNotThrow(() -> Managers.getDefault().deleteAllTasks(), "Ошибка при удалении задач, " +
                "когда их нет");
    }

    @Test
    void testGetTaskByID() {
        Task task = new Task("Read book every day", "30 pages");
        Managers.getDefault().addNewTask(task);
        assertEquals(task, Managers.getDefault().getTaskByID(task.getID()), "Нужная задача не возвращена");
        assertNull(Managers.getDefault().getTaskByID(task.getID() + 123), "Возврат не null при попытке " +
                "вызова задачи с неправильным id");
        Managers.getDefault().deleteAllTasks();
        assertNull(Managers.getDefault().getTaskByID(1), "Возврат не null при попытке вызова задачи с " +
                "несуществующим id с пустым списком задач");
    }

    @Test
    void testAddNewTask() {
        Task task = new Task("Read book every day", "30 pages");
        Managers.getDefault().addNewTask(task);
        assertEquals(task.getID(), Managers.getDefault().getTaskByID(task.getID()).getID(), "В мапу добавлена " +
                "задача с неверным id");
        Managers.getDefault().deleteAllTasks();
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Read book every day", "30 pages");
        Managers.getDefault().addNewTask(task);
        Task updateTask = Managers.getDefault().updateTask(new Task("Read book every day", "30 pages",
                TaskStatuses.IN_PROGRESS));
        assertNotEquals(updateTask, task, "Обновленная задача равнf старой версии задачи");
        Managers.getDefault().deleteAllTasks();
    }

    @Test
    void testDeleteTaskByID() {
        Task task1 = new Task("Read book every day", "30 pages");
        Task task2 = new Task("jump every day", "30 iterations");
        Managers.getDefault().addNewTask(task1);
        Managers.getDefault().addNewTask(task2);
        Managers.getDefault().deleteTaskByID(task1.getID());
        assertNull(Managers.getDefault().getTaskByID(task1.getID()), "Задача не была удалена");
        assertEquals(task2, Managers.getDefault().getTaskByID(task2.getID()), "Удалена другая задача");
        Managers.getDefault().deleteAllTasks();
    }

    @Test
    void testGetAllSubtasks() {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        assertEquals(subtasksList, Managers.getDefault().getAllSubtasks(), "Пустые листы подзадач не совпадают");
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask12);
        subtasksList.add(0, subtask11);
        subtasksList.add(1, subtask12);
        assertEquals(subtasksList, Managers.getDefault().getAllSubtasks(), "Листы подзадач не равны");
        Managers.getDefault().deleteAllSubtasks();
        Managers.getDefault().deleteAllEpics();
    }

    @Test
    void testDeleteAllSubtasks() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask12);
        Managers.getDefault().deleteAllSubtasks();
        assertEquals(new ArrayList<Subtask>(), Managers.getDefault().getAllSubtasks(), "Были удалены не все " +
                "подзадачи");
        Managers.getDefault().deleteAllEpics();
        assertDoesNotThrow(() -> Managers.getDefault().deleteAllEpics(), "Ошибка при удалении подзадач, " +
                "когда их нет");
    }

    @Test
    void testGetSubtasksByID() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask11);
        assertEquals(subtask11, Managers.getDefault().getSubtasksByID(subtask11.getID()), "Нужная подзадача " +
                "не возвращена");
        assertNull(Managers.getDefault().getSubtasksByID(subtask11.getID() + 123), "Возврат не null " +
                "при попытке вызова задачи с неправильным id");
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
        assertNull(Managers.getDefault().getSubtasksByID(1), "Возврат не null при попытке вызова " +
                "задачи с несуществующим id с пустым списком подзадач");
    }

    @Test
    void testAddNewSubtask() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask11);
        assertEquals(subtask11.getID(), Managers.getDefault().getSubtasksByID(subtask11.getID()).getID(),
                "В мапу добавлена подзадача с неверным id");
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    void testUpdateSubtask() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtaskReturnable = Managers.getDefault().updateSubtask(new Subtask("купить билеты",
                "дешёвые билеты", epic1.getID(), TaskStatuses.IN_PROGRESS));
        assertNotEquals(subtaskReturnable, subtask11, "Обновленная подзадача равна старой версии подзадачи");
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    void testDeleteSubtaskByID() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask12);
        Managers.getDefault().deleteSubtaskByID(subtask11.getID());
        assertNull(Managers.getDefault().getSubtasksByID(subtask11.getID()), "Подзадача не была удалена");
        assertEquals(subtask12, Managers.getDefault().getSubtasksByID(subtask12.getID()), "Удалена " +
                "другая задача");
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    void testGetAllEpics() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        assertEquals(epicsList, Managers.getDefault().getAllEpics(), "Пустые листы эпиков не совпадают");
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Epic epic2 = new Epic("посмотреть кино", "обязательно до конца месяца");
        Managers.getDefault().addNewEpic(epic2);
        epicsList.add(epic1);
        epicsList.add(epic2);
        assertEquals(epicsList, Managers.getDefault().getAllEpics(), "Листы эпиков не равны");
        Managers.getDefault().deleteAllEpics();
    }

    @Test
    void testDeleteAllEpics() {
        assertDoesNotThrow(() -> Managers.getDefault().deleteAllEpics(), "Ошибка при удалении эпиков, когда " +
                "их нет");
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Epic epic2 = new Epic("посмотреть кино", "обязательно до конца месяца");
        Managers.getDefault().addNewEpic(epic2);
        Managers.getDefault().deleteAllEpics();
        assertTrue(Managers.getDefault().getAllEpics().isEmpty(), "Эпики не были удалены");
    }

    @Test
    void testGetEpicByID() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        assertEquals(epic1, Managers.getDefault().getEpicByID(epic1.getID()), "Нужный эпик не возвращен");
        assertNull(Managers.getDefault().getEpicByID(epic1.getID() + 123), "Возврат не null при попытке " +
                "вызова эпика с нерпавильным id");
        Managers.getDefault().deleteAllEpics();
        assertNull(Managers.getDefault().getEpicByID(1), "Возврат не null при попытке вызова эпика с " +
                "несуществующим id с пустым списком эпиков");
    }

    @Test
    void testAddNewEpic() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        assertEquals(epic1.getID(), Managers.getDefault().getEpicByID(epic1.getID()).getID(), "В мапу " +
                "добавлен эпик с неверным id");
        Managers.getDefault().deleteAllEpics();
    }

    @Test
    void testUpdateEpic() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Epic updateEpic = Managers.getDefault().updateEpic(new Epic("Съездить в Москву",
                "обязательно до зимы", epic1.getStatus(), epic1.getID()));
        assertNotEquals(epic1, updateEpic, "Обновленный эпик равен старой аерсии эпика");
        Managers.getDefault().deleteAllEpics();
    }

    @Test
    void testDeleteEpicByID() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Epic epic2 = new Epic("посмотреть кино", "обязательно до конца месяца");
        Managers.getDefault().addNewEpic(epic2);
        Managers.getDefault().deleteEpicByID(epic1.getID());
        assertNull(Managers.getDefault().getEpicByID(epic1.getID()), "Эпик не был удалён");
        assertEquals(epic2, Managers.getDefault().getEpicByID(epic2.getID()), "Удалён другой эпик");
        Managers.getDefault().deleteAllEpics();
    }

    @Test
    void testGetAllEpicSubtasks() {
        assertNull(Managers.getDefault().getAllEpicSubtasks(1), "Возврат не null при " +
                "получении подзадач у несуществующего эпика");

        ArrayList<Subtask> subtasksList = new ArrayList<>();
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        assertEquals(subtasksList, Managers.getDefault().getAllEpicSubtasks(epic1.getID()), "Пустые " +
                "листы эпиков не совпадают");
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic1.getID());
        Managers.getDefault().addNewSubtask(subtask12);
        subtasksList.add(subtask11);
        subtasksList.add(subtask12);
        assertEquals(subtasksList, Managers.getDefault().getAllEpicSubtasks(epic1.getID()), "Листы эпиков " +
                "не равны");
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    void tryToAddSubtasksWithNonexistentEpic() {
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", 1);
        assertNull(Managers.getDefault().addNewSubtask(subtask11), "Ошибка при добавлении подзадачи " +
                " - задача прикреплена к несуществующиму эпику");
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    void testTimeFields() {
        Task task1 = new Task("Read book every day", "30 pages", LocalDateTime.now(), 60);
        Managers.getDefault().addNewTask(task1);
        System.out.println(Managers.getDefault().getTaskByID(task1.getID()).getStartTime());
        System.out.println(Managers.getDefault().getTaskByID(task1.getID()).getDuration());
        System.out.println(Managers.getDefault().getTaskByID(task1.getID()).getEndTime());

        Task task2 = new Task("jump every day", "30 iterations", LocalDateTime.now().plusDays(1), 1440);
        Managers.getDefault().addNewTask(task2);
        System.out.println(Managers.getDefault().getTaskByID(task2.getID()).getStartTime());
        System.out.println(Managers.getDefault().getTaskByID(task2.getID()).getDuration());
        System.out.println(Managers.getDefault().getTaskByID(task2.getID()).getEndTime());

        System.out.println("----------------------------------------------------------------------------------------");

        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Managers.getDefault().getEpicByID(epic1.getID());
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID(),
                LocalDateTime.now(), 43200);
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic1.getID(),
                LocalDateTime.of(2024, MAY, 28, 13, 0), 20160);
        Managers.getDefault().addNewSubtask(subtask12);

        System.out.println(epic1.getStartTime());
        System.out.println(epic1.getDuration());
        System.out.println(epic1.getEndTime());

        System.out.println("----------------------------------------------------------------------------------------");

        Managers.getDefault().deleteSubtaskByID(subtask12.getID());
        System.out.println(epic1.getStartTime());
        System.out.println(epic1.getDuration());
        System.out.println(epic1.getEndTime());

        Managers.getDefault().deleteAllTasks();
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
    }

    @Test
    void testTimeFieldsWithUploadFromFile() {

        FileBackedTasksManager fileBackedTasksManager1 = new FileBackedTasksManager(
                new File(System.getProperty("user.dir") + "\\history.txt"));

        Task task1 = new Task("Read book every day", "30 pages", LocalDateTime.now(), 60);
        fileBackedTasksManager1.addNewTask(task1);
        Task task2 = new Task("jump every day", "30 iterations", LocalDateTime.now(), 1440);
        fileBackedTasksManager1.addNewTask(task2);

        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        fileBackedTasksManager1.addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID(),
                LocalDateTime.now(), 43200);
        fileBackedTasksManager1.addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic1.getID(),
                LocalDateTime.of(2024, MAY, 28, 13, 0), 20160);
        fileBackedTasksManager1.addNewSubtask(subtask12);

        fileBackedTasksManager1.getTaskByID(task1.getID());
        fileBackedTasksManager1.getTaskByID(task2.getID());
        fileBackedTasksManager1.getEpicByID(epic1.getID());
        fileBackedTasksManager1.getSubtasksByID(subtask11.getID());
        fileBackedTasksManager1.getSubtasksByID(subtask12.getID());

        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(fileBackedTasksManager1.file);
        fileBackedTasksManager2.getAllTasks();
        fileBackedTasksManager2.getAllEpics();
        fileBackedTasksManager2.getAllSubtasks();
        Managers.getDefaultHistory().getHistory();

        Managers.getDefault().deleteAllTasks();
        Managers.getDefault().deleteAllEpics();
        Managers.getDefault().deleteAllSubtasks();
        fileBackedTasksManager1.deleteAllTasks();
        fileBackedTasksManager1.deleteAllEpics();
        fileBackedTasksManager1.deleteAllSubtasks();
        fileBackedTasksManager2.deleteAllTasks();
        fileBackedTasksManager2.deleteAllEpics();
        fileBackedTasksManager2.deleteAllSubtasks();
    }

    @Test
    void tryAddNewTaskWhileTimeIsBusy() {
        Task task1 = new Task("Read book every day", "30 pages", LocalDateTime.of(2024, MAY, 28, 13, 0), 60);
        Managers.getDefault().addNewTask(task1);
        Task task2 = new Task("jump every day", "30 iterations", LocalDateTime.of(2024, MAY, 28, 13, 0), 1440);
        Managers.getDefault().addNewTask(task2);
        Managers.getDefault().deleteAllTasks();

    }

    @Test
    void tryAddNewSubtaskWhileTimeIsBusy() {
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить одежду", "крутую одежду", epic1.getID(),
                LocalDateTime.of(2024, MAY, 28, 13, 0), 20160);
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID(),
                LocalDateTime.of(2024, MAY, 28, 13, 0), 43200);
        Managers.getDefault().addNewSubtask(subtask12);
    }
}