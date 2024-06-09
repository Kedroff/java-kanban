package ru.kedroff.project.managers.taskManager;

import ru.kedroff.project.exceptionPackage.ManagerSaveException;
import ru.kedroff.project.managers.Managers;
import ru.kedroff.project.managers.historyManager.HistoryManager;
import ru.kedroff.project.tasks.*;
import ru.kedroff.project.utils.Utils;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class FileBackedTasksManager extends InMemoryTasksManager implements TaskManager {
    public final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    private void save() throws ManagerSaveException {
        try (Writer fileWriter = new FileWriter(file.getPath())) {
            fileWriter.write("id,type,name,status,description,startTime,duration,epic\n");

            for (Map.Entry<Integer, Task> entry : tasksMap.entrySet()) {
                fileWriter.write(entry.getValue().taskToString() + "\n");
            }
            for (Map.Entry<Integer, Epic> entry : epicsMap.entrySet()) {
                fileWriter.write(entry.getValue().taskToString() + "\n");
            }
            for (Map.Entry<Integer, Subtask> entry : subtasksMap.entrySet()) {
                fileWriter.write(entry.getValue().taskToString() + "\n");
            }

            if (historyManager.getHistory() != null) {
                fileWriter.write("\n" + historyToString(historyManager));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранение данных в файл: " + e.getMessage());
        }
    }

    private Task taskFromString(String value) {
        String[] line = value.split(",");
        if (line[1].equals(TypeOfTask.TASK.toString())) {
            Task task = new Task(line[2], line[4], TaskStatuses.valueOf(line[3]),
                    Utils.formattedTime(LocalDateTime.parse(line[5])), Integer.parseInt(line[6]));
            task.setID(Integer.parseInt(line[0]));
            tasksMap.put(task.getID(), task);
            return task;
        } else if (line[1].equals(TypeOfTask.EPIC.toString())) {
            Epic epic = new Epic(line[2], line[4]);
            epic.setID(Integer.parseInt(line[0]));
            epic.setStatus(TaskStatuses.valueOf(line[3]));
            if (!line[5].isEmpty()) {
                epic.setStartTime(Utils.formattedTime(LocalDateTime.parse(line[5])));
                epic.setDuration(Integer.parseInt(line[6]));
                epic.setEndTime(epic.getStartTime().plusMinutes(epic.getDuration()));
            }
            epicsMap.put(epic.getID(), epic);
            return epic;
        } else {
            Subtask subtask = new Subtask(line[2], line[4], Integer.parseInt(line[7]), TaskStatuses.valueOf(line[3]),
                    Utils.formattedTime(LocalDateTime.parse(line[5])), Integer.parseInt(line[6]));
            subtask.setID(Integer.parseInt(line[0]));
            ArrayList<Integer> newsSubtasksIds = epicsMap.get(subtask.getIdOfEpic()).getSubtasksIds();
            newsSubtasksIds.add(subtask.getID());
            epicsMap.get(subtask.getIdOfEpic()).setSubtasksIds(newsSubtasksIds);
            subtasksMap.put(subtask.getID(), subtask);
            return subtask;
        }
    }

    private static String historyToString(HistoryManager manager) {
        StringBuilder lineOfHistory = new StringBuilder();
        for (Task task : manager.getHistory()) {
            lineOfHistory.append(task.getID());
            lineOfHistory.append(",");
        }
        return lineOfHistory.toString();
    }

    private static ArrayList<Integer> historyFromString(String value) {
        String[] split = value.split(",");
        ArrayList<Integer> historyList = new ArrayList<>();
        for (String s : split) {
            historyList.add(Integer.parseInt(s));
        }
        return historyList;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        try {
            Reader fileReader = new FileReader(file.getPath());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

            String line = bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.isEmpty()) {
                    continue;
                }
                if (Utils.isNumber(line.split(",")[1])) {
                    ArrayList<Integer> history = historyFromString(line);
                    for (int id = history.size() - 1; id > -1; id--) {
                        if (fileBackedTasksManager.tasksMap.containsKey(history.get(id))) {
                            fileBackedTasksManager.getTaskByID(history.get(id));
                        } else if (fileBackedTasksManager.epicsMap.containsKey(history.get(id))) {
                            fileBackedTasksManager.getEpicByID(history.get(id));
                        } else {
                            fileBackedTasksManager.getSubtasksByID(history.get(id));
                        }
                    }

                    return fileBackedTasksManager;
                }
                Managers.getDefaultHistory().add(fileBackedTasksManager.taskFromString(line));
            }

            return fileBackedTasksManager;
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Task getTaskByID(int id) {
        Task returnableTask = super.getTaskByID(id);
        save();
        return returnableTask;
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Subtask getSubtasksByID(int id) {
        Subtask returnableSubtask = super.getSubtasksByID(id);
        save();
        return returnableSubtask;
    }

    @Override
    public void deleteSubtaskByID(int id) {
        super.deleteSubtaskByID(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic returnableEpic = super.getEpicByID(id);
        save();
        return returnableEpic;
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public Task addNewTask(Task newTask) {
        Task returnableTask = super.addNewTask(newTask);
        save();
        return returnableTask;
    }

    @Override
    public Task updateTask(Task newTask) {
        Task returnableTask = super.updateTask(newTask);
        save();
        return returnableTask;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        Subtask returnableSubtask = super.addNewSubtask(newSubtask);
        save();
        return returnableSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask returnableSubtask = super.updateSubtask(newSubtask);
        save();
        return returnableSubtask;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        Epic returnableEpic = super.addNewEpic(newEpic);
        save();
        return returnableEpic;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic returnableEpic = super.updateEpic(newEpic);
        save();
        return returnableEpic;
    }
}
