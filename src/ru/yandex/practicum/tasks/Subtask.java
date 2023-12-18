package ru.yandex.practicum.tasks;

import ru.yandex.practicum.manager.Status;
import ru.yandex.practicum.manager.TaskType;

public class Subtask extends ru.yandex.practicum.tasks.Task {
    private Status status;
    private TaskType type;
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.status = Status.NEW;
        this.type = TaskType.SUBTASK;
    }
    @Override
    public int getId() {
        return id;
    }

    @Override
    public TaskType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", epicId=" + epicId +
                '}' + "\n";
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
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

    @Override
    public Status getStatus() {
        return status;
    }
}
