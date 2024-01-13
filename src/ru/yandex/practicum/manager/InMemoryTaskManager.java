package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    protected int generatedIds = 0;
    protected Map<Integer, Epic> epicHashMap = new HashMap<>();
    protected Map<Integer, Subtask> subtaskHashMap = new HashMap<>();
    protected Map<Integer, Task> taskHashMap = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void deleteTask() {
        taskHashMap.clear();
        historyManager.removeAllTasks();
    }

    @Override
    public void deleteSubtask() {
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            epic.cleanArraySubtasks();
            updateEpicStatus(epic);
        }
        historyManager.removeAllSubtasks();
    }

    @Override
    public void deleteEpic() {
        epicHashMap.clear();
        deleteSubtask();
        historyManager.removeAllEpics();
        historyManager.removeAllSubtasks();
    }

    @Override
    public Task getTaskByIdentify(int id) {
        Task task = taskHashMap.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Subtask getSubtaskByIdentify(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicByIdentify(int id) {
        Epic epic = epicHashMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public int generateTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);
        taskHashMap.put(taskId, task);
        return taskId;
    }

    @Override
    public int generateEpic(Epic epic) {
        int epicId = generateId();
        epic.setEpicId(epicId);
        epicHashMap.put(epicId, epic);
        return epicId;
    }

    @Override
    public Integer generateSubtask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicId());

        int subtaskId = generateId();
        subtask.setId(subtaskId);

        epic.addSubtasksIds(subtaskId);
        subtaskHashMap.put(subtaskId, subtask);
        updateEpicStatus(epic);

        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        Task savedTask = taskHashMap.get(task.getId());
        if (savedTask == null) {
            return;
        }
        taskHashMap.put(task.getId(), savedTask);

    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = subtaskHashMap.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        Epic savedEpic = epicHashMap.get(subtask.getEpicId());
        if (savedEpic == null) {
            return;
        }
        subtaskHashMap.put(subtask.getId(), savedSubtask);
        updateEpicStatus(savedEpic);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epicHashMap.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        List<Integer> subtasksIds = epic.getSubtasksIds();
        if (subtasksIds.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        Status status = null;
        for (Integer subtaskId : subtasksIds) {
            Subtask sub = subtaskHashMap.get(subtaskId);

            if (status == null) {
                status = sub.getStatus();
                continue;
            }

            if (status.equals(sub.getStatus()) && !status.equals(Status.IN_PROGRESS))  {
                continue;
            }
            status = Status.IN_PROGRESS;
                break;
        }
        epic.setStatus(status);
    }

    @Override
    public void updateTaskStatus(Task task, Status status) {
        task.setStatus(status);
    }

    @Override
    public void updateSubtaskStatus(Subtask subtask, Status status) {
        subtask.setStatus(status);
        Epic epic = epicHashMap.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    @Override
    public void deleteTaskByIdentify(int id) {
        if (taskHashMap.containsKey(id)) {
            taskHashMap.remove(id);
        }
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskByIdentify(int id) {
        Subtask subtask = subtaskHashMap.get(id);

        if (subtask != null) {
            int epicIdInSub = subtask.getEpicId();
            Epic epic = epicHashMap.get(epicIdInSub);

            subtaskHashMap.remove(id);
            historyManager.remove(id);

            if (epic != null) {
                List<Integer> ids = epic.getSubtasksIds();
                ids.remove(Integer.valueOf(id));
            }

        }
    }

    @Override
    public void deleteEpicByIdentify(int id) {
        List<Integer> subtasksToDelete = epicHashMap.get(id).getSubtasksIds();

        if (epicHashMap.containsKey(id)) {
            epicHashMap.remove(id);
            historyManager.remove(id);
        }

        for (Integer i : subtasksToDelete) {

            subtaskHashMap.remove(i);
            historyManager.remove(i);
        }

    }

    @Override
    public Map<Integer, Epic> getEpicHashMap() {
        return null;
    }

    @Override
    public Map<Integer, Subtask> getSubtaskHashMap() {
        return null;
    }

    @Override
    public Map<Integer, Task> getTaskHashMap() {
        return null;
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
