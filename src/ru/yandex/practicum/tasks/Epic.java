package ru.yandex.practicum.tasks;

import ru.yandex.practicum.manager.InMemoryTaskManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtasksIds;
    private final InMemoryTaskManager manager = new InMemoryTaskManager();

    public Epic(String name, String description) {
        super(name, description);
        subtasksIds = new ArrayList<>();
        this.status = Status.NEW;
        this.duration = 0;
        this.startTime = null;
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

    @Override
    public LocalDateTime getEndTime() {   //
        if (!subtasksIds.isEmpty()) {
            LocalDateTime earliestStartTime = null;
            LocalDateTime latestEndTime = null;

            for (int subtaskId : subtasksIds) {
                Subtask subtask = manager.getSubtaskByIdentify(subtaskId);
                LocalDateTime subtaskStartTime = subtask.getStartTime();
                LocalDateTime subtaskEndTime = subtask.getEndTime();

                if (subtaskStartTime != null && (earliestStartTime == null || subtaskStartTime.isBefore(earliestStartTime))) {
                    earliestStartTime = subtaskStartTime;
                }

                if (subtaskEndTime != null && (latestEndTime == null || subtaskEndTime.isAfter(latestEndTime))) {
                    latestEndTime = subtaskEndTime;
                }
            }

            if (earliestStartTime != null && latestEndTime != null) {
                startTime = earliestStartTime;
                return latestEndTime;
            }
        }

        return null;
    }
}
