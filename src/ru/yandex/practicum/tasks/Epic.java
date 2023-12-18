package ru.yandex.practicum.tasks;

import ru.yandex.practicum.manager.Status;
import ru.yandex.practicum.manager.TaskType;

import java.util.ArrayList;

public class Epic extends ru.yandex.practicum.tasks.Task {
    private Status status;
    private TaskType type;
    ArrayList<Integer> subtasksIds;

    private int epicId;

    public Epic(String name, String description) {
        super(name, description);
        subtasksIds = new ArrayList<>();
        this.status = Status.NEW;
        this.type = TaskType.EPIC;
    }
    @Override
    public int getId() {
        return id;
    }
    @Override
    public TaskType getType() {
        return type;
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
