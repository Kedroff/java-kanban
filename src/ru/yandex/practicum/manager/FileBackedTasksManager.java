package ru.yandex.practicum.manager;

import ru.yandex.practicum.exceptionPackage.ManagerSaveException;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        try {
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            List<Integer> history = Collections.emptyList();
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    history = CSVFormatter.historyFromString(lines[i + 1]);
                    break;
                }
                final Task task = CSVFormatter.fromString(line);
                final int id = task.getId();
                if (id > generatorId) {
                    generatorId = id;
                }
                switch (task.getType()) {
                    case TASK:
                        taskManager.tasks.put(id, task);
                        break;
                    case SUBTASK:
                        taskManager.subtasks.put(id, (Subtask) task);
                        break;
                    case EPIC:
                        taskManager.epics.put(id, (Epic) task);
                        break;
                }
            }
            for (Map.Entry<Integer, Subtask> entry : taskManager.subtasks.entrySet()) {
                final Subtask subtask = entry.getValue();
                final Epic epic = taskManager.epics.get(subtask.getEpicId());
                epic.addSubtasksIds(subtask.getId());
            }
            for (Integer taskId : history) {
                Task task = taskManager.tasks.get(taskId);
                if (task != null) {
                    taskManager.historyManager.add(task);
                }
            }
            taskManager.generatedIds = generatorId;
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно прочитать файл: " + file.getName(), e);
        }
        return taskManager;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("TaskId,Type,Description,Status,Description2,EpicId");
            writer.newLine();

            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                final Task task = entry.getValue();
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer, Subtask> entry : subtasks.entrySet()) {
                final Subtask subtask = entry.getValue();
                writer.write(CSVFormatter.toString(subtask));
                writer.newLine();
            }

            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                final Task task = entry.getValue();
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }

            writer.newLine();
            writer.write(CSVFormatter.toString(historyManager));
            writer.newLine();

        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно прочитать файл: " + file.getName(), e);
        }
    }

    @Override
    public Task getTaskByIdentify(int id) {
        final Task task = super.getTaskByIdentify(id);
        save();
        return task;
    }

    @Override
    public Subtask getSubtaskByIdentify(int id) {
        final Subtask subtask = super.getSubtaskByIdentify(id);
        save();
        return subtask;
    }

    @Override
    public Epic getEpicByIdentify(int id) {
        final Epic epic = super.getEpicByIdentify(id);
        save();
        return epic;
    }

    @Override
    public void deleteTask() {
        super.deleteTask();
        save();
    }

    @Override
    public void deleteSubtask() {
        super.deleteSubtask();
        save();
    }

    @Override
    public void deleteEpic() {
        super.deleteEpic();
        save();
    }

    @Override
    public int generateTask(Task task) {
        int id = super.generateTask(task);
        save();
        return id;
    }

    @Override
    public int generateEpic(Epic epic) {
        int id = super.generateEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer generateSubtask(Subtask subtask) {
        Integer id = super.generateSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskByIdentify(int id) {
        super.deleteTaskByIdentify(id);
        save();
    }

    @Override
    public void deleteSubtaskByIdentify(int id) {
        super.deleteSubtaskByIdentify(id);
        save();
    }

    @Override
    public void deleteEpicByIdentify(int id) {
        super.deleteEpicByIdentify(id);
        save();
    }

    public static void main(String[] args) {
        File file = new File("myFile.csv");
        FileBackedTasksManager manager = new FileBackedTasksManager(file);

        Task task1 = new Task("Покормить кота", "Кормить влажным кормом");
        manager.generateTask(task1);

        Task task2 = new Task("Помыть посуду", "Помыть тарелки и ложки");
        manager.generateTask(task2);

        Epic epic1 = new Epic("Учеба", "Нужно учиться");
        int epicId1 = manager.generateEpic(epic1);

        Subtask subtask1 = new Subtask("Методы", "Выучить методы", epicId1);
        manager.generateSubtask(subtask1);

        Subtask subtask2 = new Subtask("Классы", "Выучить классы", epicId1);
        manager.generateSubtask(subtask2);


        FileBackedTasksManager loadedManager = FileBackedTasksManager.loadFromFile(file);

        // не могу понять почему всегда пишет false, может быть из-за неправильного переопределения equals и hashCode

        System.out.println("Проверка задач: " + loadedManager.tasks.values().equals(manager.tasks.values()));
        System.out.println("Проверка подзадач: " + loadedManager.subtasks.values().equals(manager.subtasks.values()));
        System.out.println("Проверка эпиков: " + loadedManager.epics.values().equals(manager.epics.values()));
        System.out.println("Проверка истории: " + loadedManager.historyManager.equals(manager.historyManager));

        // а вот по ключам true О_о

        System.out.println("Сравнение задач по ключам: " + loadedManager.tasks.keySet().equals(manager.tasks.keySet()));
        System.out.println("Сравнение подзадач по ключам: " + loadedManager.subtasks.keySet().equals(manager.subtasks.keySet()));
        System.out.println("Сравнение эпиков по ключам: " + loadedManager.epics.keySet().equals(manager.epics.keySet()));
        System.out.println("Сравнение истории: " + loadedManager.historyManager.equals(manager.historyManager));
    }
}
