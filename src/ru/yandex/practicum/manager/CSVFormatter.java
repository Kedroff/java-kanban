package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class CSVFormatter {
    private CSVFormatter() {
    }

    public static String toString(Task task){
        if (task.getType().equals(TaskType.SUBTASK)){
            Subtask sub = (Subtask) task;
            return sub.getId() + "," + sub.getType() + "," + sub.getName() + "," + sub.getStatus() + "," + sub.getDescription() + "," + sub.getEpicId();
        } else {
            return task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription();
        }
    }

    public static Task fromString(String value){
        final String[] line = value.split(",");
        final TaskType type = TaskType.valueOf(line[1]);
        final String name = line[2];
        final String description = line[4];
        if (type.equals(TaskType.SUBTASK)) {
            final int epicId = Integer.parseInt(line[5]);
            return new Subtask(name, description, epicId);
        } else if(type.equals(TaskType.TASK)){
            return new Task(name, description);
        } else {
            return new Epic(name, description);
        }
    }


    public static String toString(HistoryManager manager){
        final List<Task> history = manager.getHistory();
        if (history.isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(history.get(0).getId());
        for (int i = 1; i < history.size(); i++){
            Task task = history.get(i);
            sb.append(",");
            sb.append(task.getId());
        }
        return sb.toString();
    }

    public static List<Integer> historyFromString(String value){
        final String[] values = value.split(",");
        final ArrayList<Integer> Ids = new ArrayList<>(values.length);
        for (String id : values) {
            Ids.add(Integer.parseInt(id));
        }
        return Ids;
    }
}
