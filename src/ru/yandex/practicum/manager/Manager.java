package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    private int generatedIds = 0;
    HashMap<Integer, Epic> epicHashMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskHashMap = new HashMap<>();
    HashMap<Integer, Task> taskHashMap = new HashMap<>();

    public ArrayList<Task> printTask() {                 // С методами Print попробовал по разному возвращать
        ArrayList<Task> task = new ArrayList<>();        // списки задач
        task.addAll(taskHashMap.values());
        return task;
    }

    public ArrayList<Subtask> printSubtask() {
        ArrayList<Subtask> subtask = new ArrayList<>();
        for (Subtask subtask1 : subtaskHashMap.values()) {
            subtask.add(subtask1);
        }
        return subtask;
    }

    public ArrayList<Epic> printEpic() {
        return new ArrayList<>(epicHashMap.values());
    }

    public void deleteTask() {
        taskHashMap.clear();
    }

    public void deleteSubtask() {
        subtaskHashMap.clear();
        for (Epic epic : epicHashMap.values()) {
            epic.cleanArraySubtasks();
        }
    }

    public void deleteEpic() {                  // Ростислав, у нас по сути при удалении эпика удаляются и все сабтаски
        epicHashMap.clear();                    // Поэтому я думаю можно вызвать метод удаления всех сабтасок
        deleteSubtask();
    }

    public Task getTaskByIdentify(int id) {
        return taskHashMap.get(id);
    }

    public Subtask getSubtaskByIdentify(int id) {
        return subtaskHashMap.get(id);
    }

    public Epic getEpicByIdentify(int id) {
        return epicHashMap.get(id);
    }

    public int generateTask(Task task) {
        int taskId = generateId();
        task.setId(taskId);
        taskHashMap.put(taskId, task);
        return taskId;
    }

    public int generateEpic(Epic epic) {
        int epicId = generateId();
        epic.setEpicId(epicId);
        epicHashMap.put(epicId, epic);
        return epicId;
    }

    public Integer generateSubtask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicId());

        int subtaskId = generateId();
        subtask.setId(subtaskId);

        epic.addSubtasksIds(subtaskId);
        subtaskHashMap.put(subtaskId, subtask);
        updateEpicStatus(epic);

        return subtaskId;
    }

    public void updateTask(Task task) {
        Task savedTask = taskHashMap.get(task.getId());
        if (savedTask == null) {
            return;
        }
        taskHashMap.put(task.getId(), savedTask);

    }

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

    public void updateEpic(Epic epic) {                         // Ростислав, не совсем понял про создание нового Эпика
        Epic savedEpic = epicHashMap.get(epic.getId());         // Я же вроде как создаю savedEpic и получаю в него
        if (savedEpic == null) {                                // данные из передаваемого :(
            return;
        }
        savedEpic.setName(epic.getName());                      // Может вы это имели ввиду?
        savedEpic.setDescription(epic.getDescription());
        epicHashMap.put(epic.getId(), savedEpic);
    }

    public void updateEpicStatus(Epic epic) {
        ArrayList<Integer> subtasksIds = epic.getSubtasksIds();
        if (subtasksIds.isEmpty()) {
            epic.setStatus("NEW");
            return;
        }

        String status = null;
        for (Integer subtaskId : subtasksIds) {
            Subtask sub = subtaskHashMap.get(subtaskId);

            if (status == null) {
                status = sub.getStatus();
                continue;
            }

            if (status.equals(sub.getStatus()) && !status.equals("IN_PROGRESS")) {
                continue;
            }
            status = "IN_PROGRESS";
        }
        epic.setStatus(status);
    }

    public void updateTaskStatus(Task task, String status) {
        task.setStatus(status);
    }

    public void updateSubtaskStatus(Subtask subtask, String status) {
        subtask.setStatus(status);
        Epic epic = epicHashMap.get(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    public void deleteTaskByIdentify(int id) {
        if (taskHashMap.containsKey(id)) {
            taskHashMap.remove(id);
        }
    }

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

    public int generateId() {
        generatedIds++;
        return generatedIds;
    }
}
