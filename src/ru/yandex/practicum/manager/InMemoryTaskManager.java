package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int generatedIds = 0;
    HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    HashMap<Integer, Task> taskHashMap = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();


    @Override
    public ArrayList<Task> printTask() {
        ArrayList<Task> task = new ArrayList<>();
        task.addAll(taskHashMap.values());
        return task;
    }

    @Override
    public ArrayList<Subtask> printSubtask() {
        ArrayList<Subtask> subtask = new ArrayList<>();
        for (Subtask subtask1 : subtaskHashMap.values()) {
            subtask.add(subtask1);
        }
        return subtask;
    }

    @Override
    public ArrayList<Epic> printEpic() {
        return new ArrayList<>(epicHashMap.values());
    }

    @Override
    public void deleteTask() {
        taskHashMap.clear();
    }

    @Override
    public void deleteSubtask() {
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            epic.cleanArraySubtasks();
        }
    }

    @Override
    public void deleteEpic() {
        epicHashMap.clear();
        deleteSubtask();
    }

    @Override
    public Task getTaskByIdentify(int id) {
        Task task = taskHashMap.get(id);
        historyManager.add(task);
        return taskHashMap.get(id);
    }

    @Override
    public Subtask getSubtaskByIdentify(int id) {
        Subtask subtask = subtaskHashMap.get(id);
        historyManager.add(subtask);
        return subtaskHashMap.get(id);
    }

    @Override
    public Epic getEpicByIdentify(int id) {
        Epic epic = epicHashMap.get(id);
        historyManager.add(epic);
        return epicHashMap.get(id);
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
        epicHashMap.put(epic.getId(), savedEpic);
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        if (subtasksIds.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }

        Status status = null;
        for (Integer subtaskId : subtasksIds) {
            Subtask sub = subtaskHashMap.get(subtaskId);

            if (status == null) {
                status = sub.getStatus();
                continue;
            }

            if (status.equals(sub.getStatus()) && !status.equals("IN_PROGRESS")) {
                continue;
            }
            status = Status.IN_PROGRESS;
        }
        epic.setStatus("NEW");
    }

    @Override
    public void updateTaskStatus(Task task, String status) {
        task.setStatus(status);
    }

    @Override
    public void updateSubtaskStatus(Subtask subtask, String status) {
        subtask.setStatus(status);
        Epic epic = epicHashMap.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    @Override
    public void deleteTaskByIdentify(int id) {
        if (taskHashMap.containsKey(id)) {
            taskHashMap.remove(id);
        }
    }

    @Override
    public void deleteSubtaskByIdentify(int id) {
        Subtask subtask = subtaskHashMap.get(id);

        if (subtask != null) {
            int epicIdInSub = subtask.getEpicId();
            Epic epic = null;

            subtaskHashMap.remove(id);

            for (Epic value : epicHashMap.values()) {
                if (value.getEpicId() == epicIdInSub) {
                    epic = value;
                    break;
                }
            }
            if (epic != null) {
                ArrayList<Integer> ids = epic.getSubtasksIds();
                ids.remove(Integer.valueOf(id));
            }

        }
    }

    @Override
    public void deleteEpicByIdentify(int id) {
        if (epicHashMap.containsKey(id)) {
            epicHashMap.remove(id);
        }
        ArrayList<Integer> subtasksToDelete = new ArrayList<>();
        for (Integer i : subtaskHashMap.keySet()) {
            Subtask deleteSub = subtaskHashMap.get(i);
            if (deleteSub != null) {
                if (deleteSub.getEpicId() == id) {
                    subtasksToDelete.add(i);
                }
            }
        }
        for (Integer i : subtasksToDelete) {
            subtaskHashMap.remove(i);
        }

    }

    @Override
    public int generateId() {
        generatedIds++;
        return generatedIds;
    }

    public List<Task> getHistory(){
       return historyManager.getHistory();
    }

}
