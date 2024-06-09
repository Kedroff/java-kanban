package ru.kedroff.project.tasks;

import ru.kedroff.project.utils.Utils;

import java.time.LocalDateTime;

public class Subtask extends Task {
    private int idOfEpic;

    public int getIdOfEpic() {
        return idOfEpic;
    }

    public void setIdOfEpic(int idOfEpic) {
        this.idOfEpic = idOfEpic;
    }

    public Subtask(String name, String additionalInformation, int idOfEpic) {
        super(name, additionalInformation);
        this.idOfEpic = idOfEpic;
        type = TypeOfTask.SUBTASK;
    }

    public Subtask(String name, String additionalInformation, int idOfEpic, LocalDateTime startTime,
                   int duration) {
        super(name, additionalInformation, Utils.formattedTime(startTime), duration);
        this.idOfEpic = idOfEpic;
        type = TypeOfTask.SUBTASK;
    }

    public Subtask(String name, String additionalInformation, int idOfEpic, TaskStatuses status) {
        super(name, additionalInformation, status);
        this.idOfEpic = idOfEpic;
        type = TypeOfTask.SUBTASK;
    }

    public Subtask(String name, String additionalInformation, int idOfEpic, TaskStatuses status, int id) {
        super(name, additionalInformation, status, id);
        this.idOfEpic = idOfEpic;
        type = TypeOfTask.SUBTASK;
    }

    public Subtask(String name, String additionalInformation, int idOfEpic, TaskStatuses status, LocalDateTime startTime,
                   int duration) {
        super(name, additionalInformation, status, Utils.formattedTime(startTime), duration);
        this.idOfEpic = idOfEpic;
        type = TypeOfTask.SUBTASK;
    }

    public Subtask(String name, String additionalInformation, int idOfEpic, TaskStatuses status, int id,
                   LocalDateTime startTime, int duration) {
        super(name, additionalInformation, status, id, Utils.formattedTime(startTime), duration);
        this.idOfEpic = idOfEpic;
        type = TypeOfTask.SUBTASK;
    }

    @Override
    public String taskToString() {
        String line = this.getID() +
                "," +
                this.getType() +
                "," +
                this.getName() +
                "," +
                this.getStatus() +
                "," +
                this.getAdditionalInformation() +
                "," +
                this.getStartTime() +
                "," +
                this.getDuration() +
                "," +
                this.getIdOfEpic();

        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return type == subtask.type && name.equals(subtask.name) && status == subtask.status
                && id == subtask.id && additionalInformation.equals(subtask.additionalInformation)
                && idOfEpic == subtask.idOfEpic;
    }
}