package ru.yandex.practicum.tasks;

import ru.yandex.practicum.manager.Status;

public class Subtask extends ru.yandex.practicum.tasks.Task {
    private Status status;
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.status = Status.NEW;
    }


    @Override
    public String toString() {
        return "Subtask{" +
                "status='" + status + '\'' +
                ", epicId=" + epicId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
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
