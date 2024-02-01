package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CSVFormatter {
    private CSVFormatter() {
    }

    public static String toString(Task task) {
        String result = task.getId() + "," + task.getType() + "," + task.getName() + "," + task.getStatus() + "," + task.getDescription() + "," + task.getStartTime() + "," + task.getEndTime() + "," + task.getDuration();
        if (task.getType().equals(TaskType.SUBTASK)) {
            Subtask sub = (Subtask) task;
            return result + "," + sub.getEpicId();
        }
        return result;
    }


    public static Task fromString(String value) {
        final String[] line = value.split(",");
        final int taskId = Integer.parseInt(line[0]);
        final TaskType type = TaskType.valueOf(line[1]);
        final String name = line[2];
        final Status status = Status.valueOf(line[3]);
        final String description = line[4];
        final LocalDateTime startTime = setTime(line[5]);
        final LocalDateTime endTime = setTime(line[6]);
        final int duration = setDuration(line[7]);

        if (type.equals(TaskType.SUBTASK)) {
            final int epicId = Integer.parseInt(line[5]);
            final Subtask subtask = new Subtask(name, description, epicId);
            subtask.setStatus(status);
            subtask.setId(taskId);
            subtask.setStartTime(startTime);
            subtask.setEndTime(endTime);
            subtask.setDuration(duration);
            return subtask;
        } else if (type.equals(TaskType.TASK)) {
            final Task task = new Task(name, description);
            task.setStatus(status);
            task.setId(taskId);
            task.setStartTime(startTime);
            task.setEndTime(endTime);
            task.setDuration(duration);
            return task;
        } else {
            final Epic epic = new Epic(name, description);
            epic.setStatus(status);
            epic.setId(taskId);
            epic.setStartTime(startTime);
            epic.setEndTime(endTime);
            epic.setDuration(duration);
            return epic;
        }
    }


    public static String toString(HistoryManager manager) {
        final List<Task> history = manager.getHistory();
        if (history.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(history.get(0).getId());
        for (int i = 1; i < history.size(); i++) {
            Task task = history.get(i);
            sb.append(",");
            sb.append(task.getId());
        }
        return sb.toString();
    }

    public static List<Integer> historyFromString(String value) {
        final String[] values = value.split(",");
        final ArrayList<Integer> Ids = new ArrayList<>(values.length);
        for (String id : values) {
            Ids.add(Integer.parseInt(id));
        }
        return Ids;
    }

    private static LocalDateTime setTime(String value){
        if (value.equals("null")){
            return null;
        }else{
            return LocalDateTime.parse(value);
        }
    }

    private static int setDuration(String value){
        if (value.equals("null")){
            return 0;
        }else{
            return Integer.parseInt(value);
        }
    }
}
