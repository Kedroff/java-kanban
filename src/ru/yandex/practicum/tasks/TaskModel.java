package ru.yandex.practicum.tasks;

import ru.yandex.practicum.utils.Utils;

import java.time.LocalDateTime;

public class TaskModel {
    protected TypeOfTask type;
    protected String name;
    protected TaskStatuses status;
    protected int id = 0;
    protected String additionalInformation;
    protected int duration = 0;
    protected LocalDateTime startTime = null;

    public TypeOfTask getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatuses getStatus() {
        return status;
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration);
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = Utils.formattedTime(startTime);
    }

    public LocalDateTime getStartTime() {
        return Utils.formattedTime(this.startTime);
    }

    public TaskModel(String name, String additionalInformation) {
        this.name = name;
        this.additionalInformation = additionalInformation;
        this.status = TaskStatuses.NEW;
        type = TypeOfTask.TASK;
    }

    public TaskModel(String name, String additionalInformation, LocalDateTime startTime,
                     int duration) {
        this.name = name;
        this.additionalInformation = additionalInformation;
        this.status = TaskStatuses.NEW;
        this.startTime = Utils.formattedTime(startTime);
        this.duration = duration;
        type = TypeOfTask.TASK;
    }

    public TaskModel(String name, String additionalInformation, TaskStatuses status) {
        this.name = name;
        this.additionalInformation = additionalInformation;
        this.status = status;
        type = TypeOfTask.TASK;
    }

    public TaskModel(String name, String additionalInformation, TaskStatuses status, int id) {
        this.name = name;
        this.additionalInformation = additionalInformation;
        this.status = status;
        this.id = id;
        type = TypeOfTask.TASK;
    }

    public TaskModel(String name, String additionalInformation, TaskStatuses status, LocalDateTime startTime,
                     int duration) {
        this.name = name;
        this.additionalInformation = additionalInformation;
        this.status = status;
        this.startTime = Utils.formattedTime(startTime);
        this.duration = duration;
        type = TypeOfTask.TASK;
    }

    public TaskModel(String name, String additionalInformation, TaskStatuses status, int id, LocalDateTime startTime,
                     int duration) {
        this.name = name;
        this.additionalInformation = additionalInformation;
        this.status = status;
        this.id = id;
        this.startTime = Utils.formattedTime(startTime);
        this.duration = duration;
        type = TypeOfTask.TASK;
    }

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
                ",";

        return line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskModel task = (TaskModel) o;
        return type == task.type && name.equals(task.name) && status == task.status
                && id == task.id && additionalInformation.equals(task.additionalInformation);
    }

}

