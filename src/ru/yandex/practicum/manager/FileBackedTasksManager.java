package ru.yandex.practicum.manager;

import ru.yandex.practicum.ExceptionPackage.ManagerSaveException;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
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
    private File file;
    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        final FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        try{
            final String csv = Files.readString(file.toPath());
            final String[] lines = csv.split(System.lineSeparator());
            int generatorId = 0;
            List<Integer> history = Collections.emptyList();
            for (int i = 0; i<lines.length;i++){
                String line = lines[i];
                if (line.isEmpty()){
                    history = CSVFormatter.historyFromString(lines[i+1]);
                    break;
                }
                final Task task = CSVFormatter.fromString(line);
                final int id = task.getId();
                if (id > generatorId) {
                    generatorId = id;
                }
                taskManager.generateTask(task);
            }
            for (Map.Entry<Integer,Subtask> entry : taskManager.subtaskHashMap.entrySet()){
                final Subtask subtask = entry.getValue();
                final Epic epic = taskManager.epicHashMap.get(subtask.getEpicId());
                epic.addSubtasksIds(subtask.getId());
            }
            for (Integer taskId : history){
                taskManager.historyManager.add(taskManager.getTaskByIdentify(taskId));
            }
            taskManager.generatedIds = generatorId;
        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно прочитать файл: " + file.getName(),e);
        }
        return taskManager;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {

            for (Map.Entry<Integer,Task> entry : taskHashMap.entrySet()){
                final Task task = entry.getValue();
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }

            for (Map.Entry<Integer,Subtask> entry : subtaskHashMap.entrySet()){
                final Subtask subtask = entry.getValue();
                writer.write(CSVFormatter.toString(subtask));
                writer.newLine();
            }

            for (Map.Entry<Integer,Epic> entry : epicHashMap.entrySet()){
                final Task task = entry.getValue();
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }

                writer.newLine();
                writer.write(CSVFormatter.toString(historyManager));
                writer.newLine();

        } catch (IOException e) {
            throw new ManagerSaveException("Невозможно прочитать файл: " + file.getName(),e);
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
        taskHashMap.put(id, task);
        save();
        return id;
    }

    @Override
    public int generateEpic(Epic epic) {
        int id =  super.generateEpic(epic);
        epicHashMap.put(id, epic);
        save();
        return id;
    }

    @Override
    public Integer generateSubtask(Subtask subtask) {
        Integer id = super.generateSubtask(subtask);
        subtaskHashMap.put(id, subtask);
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
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public void updateTaskStatus(Task task, Status status) {
        super.updateTaskStatus(task, status);
        save();
    }

    @Override
    public void updateSubtaskStatus(Subtask subtask, Status status) {
        super.updateSubtaskStatus(subtask, status);
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
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("myFile.csv"));

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

        manager.getSubtaskByIdentify(subtask1.getId());
        manager.getEpicByIdentify(epicId1);

        FileBackedTasksManager newManager = FileBackedTasksManager.loadFromFile(new File("myFile.csv"));

        System.out.println("История просмотра восстановлена: " + newManager.historyManager);

        System.out.println("Задачи в новом менеджере: " + newManager.taskHashMap.values());
    }
}
