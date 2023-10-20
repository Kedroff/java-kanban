package ru.yandex.practicum.tasks;

public class Subtask extends ru.yandex.practicum.tasks.Task {
    private String status;
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.status = "NEW";
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
        this.status = status;
    }

    @Override
    public String getStatus() {
        return status;
    }
}
