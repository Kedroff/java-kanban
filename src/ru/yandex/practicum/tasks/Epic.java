package ru.yandex.practicum.tasks;

import ru.yandex.practicum.manager.Status;

import java.util.ArrayList;

public class Epic extends ru.yandex.practicum.tasks.Task {
    private Status status;
    ArrayList<Integer> subtasksIds;

    private int epicId;

    public Epic(String name, String description) {
        super(name, description);
        subtasksIds = new ArrayList<>();
        this.status = Status.NEW;
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

    public void addSubtasksIds(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        switch (Status.valueOf(status)){
            case NEW:
                this.status = Status.NEW;
                break;
            case IN_PROGRESS:
                this.status = Status.IN_PROGRESS;
                break;
            case DONE:
                this.status = Status.DONE;
                break;
        }
    }

    public void cleanArraySubtasks() {
        subtasksIds.clear();
    }

}
