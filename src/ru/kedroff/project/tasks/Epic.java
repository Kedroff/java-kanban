package ru.kedroff.project.tasks;

import ru.kedroff.project.utils.Utils;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksIds = new ArrayList<>();
    private LocalDateTime endTime = null;

    public ArrayList<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    public void setSubtasksIds(ArrayList<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    public Epic(String name, String additionalInformation) {
        super(name, additionalInformation);
        type = TypeOfTask.EPIC;
    }

    public Epic(String name, String additionalInformation, TaskStatuses taskStatuses, int id) {
        super(name, additionalInformation, taskStatuses, id);
        type = TypeOfTask.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return Utils.formattedTime(endTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = Utils.formattedTime(endTime);
    }

    @Override
    public String taskToString() {
        StringBuilder line = new StringBuilder();
        line.append(this.getID());
        line.append(",");
        line.append(this.getType());
        line.append(",");
        line.append(this.getName());
        line.append(",");
        line.append(this.getStatus());
        line.append(",");
        line.append(this.getAdditionalInformation());
        line.append(",");
        if (this.getStartTime() != null) {
            line.append(this.getStartTime());
            line.append(",");
        } else {
            line.append(",");
        }

        line.append(this.getDuration());
        line.append(",");

        return line.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return type == epic.type && name.equals(epic.name) && status == epic.status
                && id == epic.id && additionalInformation.equals(epic.additionalInformation)
                && subtasksIds.equals(epic.subtasksIds);
    }
}
