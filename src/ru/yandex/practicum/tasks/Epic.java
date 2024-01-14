package ru.yandex.practicum.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksIds;


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

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksIds=" + subtasksIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return subtasksIds.equals(epic.subtasksIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasksIds);
    }
}
