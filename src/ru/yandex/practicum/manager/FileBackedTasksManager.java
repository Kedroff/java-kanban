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
    private static final String CSV_HEADER = "id,type,name,status,description,epic,start,end,duration";
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
                        taskManager.prioritizedTasks.add(task);
                        break;
                    case SUBTASK:
                        taskManager.subtasks.put(id, (Subtask) task);
                        taskManager.prioritizedTasks.add(task);
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
                } else if ((task = taskManager.subtasks.get(taskId)) != null) {
                    taskManager.historyManager.add(task);
                } else if ((task = taskManager.epics.get(taskId)) != null) {
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
            writer.write(CSV_HEADER);
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

        System.out.println("Проверка задач: " + loadedManager.getTaskList().equals(manager.getTaskList()));
        System.out.println("Проверка подзадач: " + loadedManager.getSubtaskList().equals(manager.getSubtaskList()));
        System.out.println("Проверка эпиков: " + loadedManager.getEpicList().equals(manager.getEpicList()));
        System.out.println("Проверка истории: " + loadedManager.getHistory().equals(manager.getHistory()));

        System.out.println("Сравнение значения идентификатора последней добавленной задачи: " + (loadedManager.generatedIds == manager.generatedIds));
    }
}
