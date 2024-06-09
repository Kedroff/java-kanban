package ru.kedroff.project.managers.taskManager;

import ru.kedroff.project.exceptionPackage.TaskInputDateException;
import ru.kedroff.project.managers.Managers;
import ru.kedroff.project.managers.historyManager.HistoryManager;
import ru.kedroff.project.tasks.Epic;
import ru.kedroff.project.tasks.Subtask;
import ru.kedroff.project.tasks.Task;
import ru.kedroff.project.tasks.TaskStatuses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class InMemoryTasksManager implements TaskManager {
    protected Map<Integer, Task> tasksMap = new HashMap<>();
    protected Map<Integer, Epic> epicsMap = new HashMap<>();
    protected Map<Integer, Subtask> subtasksMap = new HashMap<>();
    protected int counterID = 0;
    protected HistoryManager historyManager;

    public InMemoryTasksManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    protected boolean isIntersect(Task newTask) {
        ArrayList<Task> prioritizedTasksList = new ArrayList<>(getPrioritizedTasks());

        if (!prioritizedTasksList.isEmpty()) {
            if (newTask.getEndTime().isBefore(prioritizedTasksList.get(0).getStartTime()) ||
                    newTask.getStartTime().isAfter(prioritizedTasksList.get(prioritizedTasksList.size() - 1).getEndTime())) {
                return false;
            }

            for (int i = 1; i < prioritizedTasksList.size() - 1; i++) {
                if (newTask.getEndTime().isBefore(prioritizedTasksList.get(i).getStartTime()) &&
                        newTask.getStartTime().isAfter(prioritizedTasksList.get(i - 1).getEndTime())) {
                    return false;
                }
            }
        } else {
            return false;
        }

        return true;

    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    @Override
    public void deleteAllTasks() {
        if (!tasksMap.isEmpty()) {
            for (Map.Entry<Integer, Task> entry : tasksMap.entrySet()) {
                historyManager.remove(entry.getKey());
            }
            tasksMap.clear();
        }
    }

    @Override
    public Task getTaskByID(int id) {
        if (tasksMap.containsKey(id)) {
            historyManager.add(tasksMap.get(id));
            return tasksMap.get(id);
        }
        return null;
    }

    @Override
    public Task addNewTask(Task newTask) {
        if (newTask.getStartTime() != null) {
            try {
                if (isIntersect(newTask)) {
                    throw new TaskInputDateException("Выбранный вами промежуток выполнения задачи уже занят. " +
                            "Введите другой.");
                }
            } catch (TaskInputDateException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }

        newTask.setID(++counterID);
        tasksMap.put(newTask.getID(), newTask);
        return newTask;
    }

    @Override
    public Task updateTask(Task newTask) {
        if (newTask.getStartTime() != null) {
            Task oldTask = tasksMap.get(newTask.getID());
            tasksMap.remove(newTask.getID());
            try {
                if (isIntersect(newTask)) {
                    throw new TaskInputDateException("Выбранный вами промежуток выполнения задачи уже занят. " +
                            "Введите другой.");
                }
            } catch (TaskInputDateException e) {
                System.out.println(e.getMessage());
                tasksMap.put(oldTask.getID(), oldTask);
                return null;
            }
        }
        tasksMap.put(newTask.getID(), newTask);
        return newTask;
    }

    @Override
    public void deleteTaskByID(int id) {
        if (tasksMap.containsKey(id)) {
            historyManager.remove(id);
            tasksMap.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    @Override
    public void deleteAllSubtasks() {
        if (!subtasksMap.isEmpty()) {
            for (Map.Entry<Integer, Subtask> entry : subtasksMap.entrySet()) {
                historyManager.remove(entry.getKey());
            }
            subtasksMap.clear();
            if (!epicsMap.isEmpty()) {
                for (Epic epic : epicsMap.values()) {
                    if (!epic.getSubtasksIds().isEmpty()) {
                        epic.setSubtasksIds(new ArrayList<Integer>());
                        changeEpicStatus(epic);
                        if (epic.getStartTime() != null) {
                            changeEpicDuration(epic);
                        }
                    }
                }
            }
        }
    }

    @Override
    public Subtask getSubtasksByID(int id) {
        if (subtasksMap.containsKey(id)) {
            historyManager.add(subtasksMap.get(id));
            return subtasksMap.get(id);
        }
        return null;
    }

    @Override
    public Subtask addNewSubtask(Subtask newSubtask) {
        if (subtasksMap.containsKey(newSubtask.getID())) {
            return null;
        }
        if (!epicsMap.containsKey(newSubtask.getIdOfEpic())) {
            return null;
        }
        if (newSubtask.getStartTime() != null) {
            try {
                if (isIntersect(newSubtask)) {
                    throw new TaskInputDateException("Выбранный вами промежуток выполнения задачи уже занят. " +
                            "Введите другой.");
                }
            } catch (TaskInputDateException e) {
                System.out.println(e.getMessage());
                return null;
            }
        }
        newSubtask.setID(++counterID);
        subtasksMap.put(newSubtask.getID(), newSubtask);
        ArrayList<Integer> newEpicSubtasksIds = epicsMap.get(newSubtask.getIdOfEpic()).getSubtasksIds();
        newEpicSubtasksIds.add(newSubtask.getID());
        epicsMap.get(newSubtask.getIdOfEpic()).setSubtasksIds(newEpicSubtasksIds);
        changeEpicStatus(epicsMap.get(newSubtask.getIdOfEpic()));
        if (newSubtask.getStartTime() != null) {
            changeEpicStartTimeAndEndTime(epicsMap.get(newSubtask.getIdOfEpic()));
            changeEpicDuration(epicsMap.get(newSubtask.getIdOfEpic()));
        }
        return newSubtask;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        if (newSubtask.getStartTime() != null) {
            Subtask oldSubtask = subtasksMap.get(newSubtask.getID());
            subtasksMap.remove(newSubtask.getID());
            try {
                if (isIntersect(newSubtask)) {
                    throw new TaskInputDateException("Выбранный вами промежуток выполнения задачи уже занят. " +
                            "Введите другой.");
                }
            } catch (TaskInputDateException e) {
                System.out.println(e.getMessage());
                subtasksMap.put(oldSubtask.getID(), oldSubtask);
                return null;
            }
        }
        subtasksMap.put(newSubtask.getID(), newSubtask);
        changeEpicStatus(epicsMap.get(newSubtask.getIdOfEpic()));
        if (newSubtask.getStartTime() != null) {
            changeEpicStartTimeAndEndTime(epicsMap.get(newSubtask.getIdOfEpic()));
            changeEpicDuration(epicsMap.get(newSubtask.getIdOfEpic()));
        }
        return newSubtask;
    }

    @Override
    public void deleteSubtaskByID(int id) {
        if (subtasksMap.containsKey(id)) {
            historyManager.remove(id);
            ArrayList<Integer> newSubtasksIds = epicsMap.get(subtasksMap.get(id).getIdOfEpic()).getSubtasksIds();
            newSubtasksIds.remove((Integer) id);
            epicsMap.get(subtasksMap.get(id).getIdOfEpic()).setSubtasksIds(newSubtasksIds);
            changeEpicStatus(epicsMap.get(subtasksMap.get(id).getIdOfEpic()));
            Epic temp = epicsMap.get(subtasksMap.get(id).getIdOfEpic());
            subtasksMap.remove(id);
            if (temp.getStartTime() != null) {
                changeEpicStartTimeAndEndTime(temp);
                changeEpicDuration(temp);
            }
        }
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public void deleteAllEpics() {
        if (!epicsMap.isEmpty()) {
            for (Map.Entry<Integer, Epic> entry : epicsMap.entrySet()) {
                historyManager.remove(entry.getKey());
                for (int id : entry.getValue().getSubtasksIds()) {
                    historyManager.remove(id);
                }
            }
            epicsMap.clear();
            subtasksMap.clear();
        }
    }

    @Override
    public Epic getEpicByID(int id) {
        if (epicsMap.containsKey(id)) {
            historyManager.add(epicsMap.get(id));
            return epicsMap.get(id);
        }
        return null;
    }

    @Override
    public Epic addNewEpic(Epic newEpic) {
        newEpic.setID(++counterID);
        epicsMap.put(newEpic.getID(), newEpic);
        changeEpicStartTimeAndEndTime(newEpic);
        changeEpicDuration(newEpic);
        return newEpic;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        newEpic.setSubtasksIds(epicsMap.get(newEpic.getID()).getSubtasksIds());
        epicsMap.put(newEpic.getID(), newEpic);
        changeEpicStartTimeAndEndTime(newEpic);
        changeEpicDuration(newEpic);
        return newEpic;
    }

    @Override
    public void deleteEpicByID(int id) {
        if (epicsMap.containsKey(id)) {
            for (int idOfSubtasks : epicsMap.get(id).getSubtasksIds()) {
                historyManager.remove(idOfSubtasks);
                subtasksMap.remove(idOfSubtasks);
            }
            epicsMap.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public ArrayList<Subtask> getAllEpicSubtasks(int epicID) {
        if (!epicsMap.containsKey(epicID)) {
            return null;
        }
        return epicsMap.get(epicID).getSubtasksIds()
                .stream()
                .map(id -> {
                    return subtasksMap.get(id);
                }).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Task> getPrioritizedTasks() {
        TreeSet<Task> priorityList = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime().isAfter(task2.getStartTime())) {
                return 1;
            } else if (task1.getStartTime().isEqual(task2.getStartTime())) {
                return 0;
            } else {
                return -1;
            }
        });

        if (!tasksMap.isEmpty()) {
            for (Map.Entry<Integer, Task> entry : tasksMap.entrySet()) {
                priorityList.add(entry.getValue());
            }
        }

        if (!subtasksMap.isEmpty()) {
            for (Map.Entry<Integer, Subtask> entry : subtasksMap.entrySet()) {
                priorityList.add(entry.getValue());
            }
        }

        return new ArrayList<>(priorityList);
    }


    protected void changeEpicStatus(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setStatus(TaskStatuses.NEW);
            return;
        }
        int counterOfDone = 0;
        int counterOfNew = 0;
        for (int idOfSubtask : epic.getSubtasksIds()) {
            if (subtasksMap.get(idOfSubtask).getStatus().equals(TaskStatuses.IN_PROGRESS)) {
                epic.setStatus(TaskStatuses.IN_PROGRESS);
                return;
            }
            if (subtasksMap.get(idOfSubtask).getStatus().equals(TaskStatuses.DONE)) {
                counterOfDone++;
            } else {
                counterOfNew++;
            }
        }
        if (counterOfDone == epic.getSubtasksIds().size()) {
            epic.setStatus(TaskStatuses.DONE);
        } else if (counterOfNew == epic.getSubtasksIds().size()) {
            epic.setStatus(TaskStatuses.NEW);
        } else if (counterOfDone > 0 && counterOfNew > 0) {
            epic.setStatus(TaskStatuses.IN_PROGRESS);
        }
    }

    protected void changeEpicDuration(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            epic.setDuration(0);

        } else {
            int epicDuration = 0;
            for (int id : epic.getSubtasksIds()) {
                epicDuration += subtasksMap.get(id).getDuration();
            }
            epic.setDuration(epicDuration);
        }
    }

    protected void changeEpicStartTimeAndEndTime(Epic epic) {
        if (epic.getSubtasksIds().isEmpty()) {
            return;
        }

        boolean startTimeSet = false;
        boolean endTimeSet = false;

        for (Integer id : epic.getSubtasksIds()) {
            if (subtasksMap.containsKey(id)) {
                Subtask subtask = subtasksMap.get(id);
                if (subtask.getStartTime() != null && (!startTimeSet || epic.getStartTime().isAfter(subtask.getStartTime()))) {
                    epic.setStartTime(subtask.getStartTime());
                    startTimeSet = true;
                }
                if (subtask.getEndTime() != null && (!endTimeSet || epic.getEndTime().isBefore(subtask.getEndTime()))) {
                    epic.setEndTime(subtask.getEndTime());
                    endTimeSet = true;
                }
            }
        }
    }


}
