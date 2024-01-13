package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends ru.yandex.practicum.tasks.Task {
    private List<Integer> subtasksIds;

    private int epicId;

    public Epic(String name, String description) {
        super(name, description);
        subtasksIds = new ArrayList<>();
        this.status = Status.NEW;
    }
    @Override
    public int getId() {
        return id;
    }
    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}' + "\n";
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtasksIds(int subtaskId) {
        subtasksIds.add(subtaskId);
    }
    public void cleanArraySubtasks() {
        subtasksIds.clear();
    }

}
