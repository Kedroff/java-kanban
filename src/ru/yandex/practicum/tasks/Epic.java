package ru.yandex.practicum.tasks;

import java.util.ArrayList;

public class Epic extends ru.yandex.practicum.tasks.Task {
    private String status;
    ArrayList<Integer> subtasksIds;

    private int epicId;

    public Epic(String name, String description) {
        super(name, description);
        subtasksIds = new ArrayList<>();
        this.status = "NEW";
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
                "status='" + status + '\'' +
                ", id=" + epicId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}' + "\n";
    }

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void addSubtasksIds(int subtaskId){
        subtasksIds.add(subtaskId);
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }
}
