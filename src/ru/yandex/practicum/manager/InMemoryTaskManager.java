package ru.yandex.practicum.manager;

import ru.yandex.practicum.exceptionPackage.TaskValidationException;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int generatedIds = 0;
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<LocalDateTime, Task> prioritizedTasks = new TreeMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void deleteTask() {
        tasks.clear();
        historyManager.removeAllTasks();
    }

    @Override
    public void deleteSubtask() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.cleanArraySubtasks();
            updateEpicStatus(epic);
        }
        historyManager.removeAllSubtasks();
    }

    @Override
    public void deleteEpic() {
        epics.clear();
        deleteSubtask();
        historyManager.removeAllEpics();
    }

    @Override
    public Task getTaskByIdentify(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskByIdentify(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicByIdentify(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int generateTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);
        tasks.put(taskId, task);
        validate(task);
        prioritizedTasks.put(task.getStartTime(), task);
        return taskId;
    }

    @Override
    public int generateEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
        validate(epic);
        prioritizedTasks.put(epic.getStartTime(), epic);
        return epicId;
    }

    @Override
    public Integer generateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());

        int subtaskId = generateId();
        subtask.setId(subtaskId);

        epic.addSubtasksIds(subtaskId);
        subtasks.put(subtaskId, subtask);
        updateEpicStatus(epic);

        validate(subtask);
        prioritizedTasks.put(subtask.getStartTime(), subtask);

        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        savedTask.setName(task.getName());
        savedTask.setDescription(task.getDescription());
        savedTask.setStatus(task.getStatus());
        savedTask.setStartTime(task.getStartTime());
        tasks.put(task.getId(), savedTask);
        prioritizedTasks.put(savedTask.getStartTime(), savedTask);

    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        Epic savedEpic = epics.get(subtask.getEpicId());
        if (savedEpic == null) {
            return;
        }
        savedSubtask.setName(subtask.getName());
        savedSubtask.setDescription(subtask.getDescription());
        savedSubtask.setStatus(subtask.getStatus());
        savedSubtask.setStartTime(subtask.getStartTime());
        subtasks.put(subtask.getId(), savedSubtask);
        updateEpicStatus(savedEpic);

        validate(savedSubtask);
        prioritizedTasks.put(savedSubtask.getStartTime(), savedSubtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
        updateEpicStatus(savedEpic);

        validate(savedEpic);
        prioritizedTasks.put(savedEpic.getStartTime(), savedEpic);
    }

    private void updateEpicStatus(Epic epic) {
        List<Integer> subtasksIds = epic.getSubtasksIds();
        if (subtasksIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Status status = null;
        for (Integer subtaskId : subtasksIds) {
            Subtask sub = subtasks.get(subtaskId);

            if (status == null) {
                status = sub.getStatus();
                continue;
            }

            if (status.equals(sub.getStatus()) && !status.equals(Status.IN_PROGRESS)) {
                continue;
            }
            status = Status.IN_PROGRESS;
            break;
        }
        epic.setStatus(status);
    }

    @Override
    public void deleteTaskByIdentify(int id) {
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskByIdentify(int id) {
        Subtask subtask = subtasks.get(id);

        if (subtask != null) {
            int epicIdInSub = subtask.getEpicId();
            Epic epic = epics.get(epicIdInSub);

            subtasks.remove(id);
            historyManager.remove(id);

            if (epic != null) {
                List<Integer> ids = epic.getSubtasksIds();
                ids.remove(Integer.valueOf(id));
            }

        }
    }

    @Override
    public void deleteEpicByIdentify(int id) {
        List<Integer> subtasksToDelete = epics.get(id).getSubtasksIds();

        epics.remove(id);
        historyManager.remove(id);


        for (Integer i : subtasksToDelete) {

            subtasks.remove(i);
            historyManager.remove(i);
        }

    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks.values())
                .stream()
                .sorted(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private void validate(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();

        Integer result = prioritizedTasks.values().stream()
                .map(tsk -> {
                    if (startTime.isBefore(tsk.getEndTime()) && endTime.isBefore(tsk.getStartTime())) {
                        return 1;
                    }
                    if (startTime.isBefore(tsk.getEndTime()) && endTime.isAfter(tsk.getEndTime())) {
                        return 1;
                    }
                    return 0;
                })
                .reduce(Integer::sum)
                .orElse(0);
        if (result > 0) {
            throw new TaskValidationException("Задача пересекается");
        }
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public int generateId() {
        generatedIds++;
        return generatedIds;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
