package ru.yandex.practicum.manager;

import ru.yandex.practicum.exceptionPackage.TaskValidationException;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Status;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int generatedIds = 0;
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing((Task::getStartTime), Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task::getId));
    protected HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public void deleteTask() {
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
        }
        historyManager.removeAllTasks();
        tasks.clear();
    }

    @Override
    public void deleteSubtask() {
        for (Epic epic : epics.values()) {
            epic.cleanArraySubtasks();
            updateEpicStatus(epic);
            updateEpicTime(epic.getId());
        }
        for (Subtask sub : subtasks.values()) {
            prioritizedTasks.remove(sub);
        }
        historyManager.removeAllSubtasks();
        subtasks.clear();
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
        prioritizedTasks.add(task);
        return taskId;
    }

    @Override
    public int generateEpic(Epic epic) {
        int epicId = generateId();
        epic.setId(epicId);
        epics.put(epicId, epic);
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
        updateEpicTime(epic.getId());

        validate(subtask);
        prioritizedTasks.add(subtask);

        return subtaskId;
    }

    @Override
    public void updateTask(Task task) {
        Task savedTask = tasks.get(task.getId());
        if (savedTask == null) {
            return;
        }
        prioritizedTasks.remove(savedTask);

        savedTask.setName(task.getName());
        savedTask.setDescription(task.getDescription());
        savedTask.setStatus(task.getStatus());
        savedTask.setStartTime(task.getStartTime());
        savedTask.setDuration(task.getDuration());
        prioritizedTasks.add(savedTask);

    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask savedSubtask = subtasks.get(subtask.getId());
        if (savedSubtask == null) {
            return;
        }
        prioritizedTasks.remove(savedSubtask);

        Epic savedEpic = epics.get(subtask.getEpicId());
        if (savedEpic == null) {
            return;
        }
        savedSubtask.setName(subtask.getName());
        savedSubtask.setDescription(subtask.getDescription());
        savedSubtask.setStatus(subtask.getStatus());
        savedSubtask.setStartTime(subtask.getStartTime());
        savedSubtask.setDuration(savedSubtask.getDuration());

        updateEpicStatus(savedEpic);
        updateEpicTime(savedEpic.getId());

        validate(savedSubtask);
        prioritizedTasks.add(savedSubtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic savedEpic = epics.get(epic.getId());
        if (savedEpic == null) {
            return;
        }
        savedEpic.setName(epic.getName());
        savedEpic.setDescription(epic.getDescription());
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
        Task taskToRemove = tasks.remove(id);
        prioritizedTasks.remove(taskToRemove);
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtaskByIdentify(int id) {
        Subtask subtask = subtasks.remove(id);

        if (subtask != null) {
            int epicIdInSub = subtask.getEpicId();
            Epic epic = epics.get(epicIdInSub);

            historyManager.remove(id);
            prioritizedTasks.remove(subtask);

            if (epic != null) {
                List<Integer> ids = epic.getSubtasksIds();
                ids.remove(Integer.valueOf(id));

                updateEpicTime(epic.getId());
                updateEpicStatus(epic);
            }

        }
    }

    @Override
    public void deleteEpicByIdentify(int id) {
        List<Integer> subtasksToDelete = epics.remove(id).getSubtasksIds();

        historyManager.remove(id);

        for (Integer subtaskId : subtasksToDelete) {
            Subtask subToDelete = subtasks.remove(subtaskId);
            historyManager.remove(subtaskId);
            prioritizedTasks.remove(subToDelete);
        }

    }
    @Override
    public Set<Task> getPrioritizedTasks() {
        return new TreeSet<>(prioritizedTasks);
    }

    private void validate(Task task) {
        LocalDateTime startTime = task.getStartTime();
        if (startTime == null){
            return;
        }
        LocalDateTime endTime = task.getEndTime();

        for (Task taskPriority : prioritizedTasks) {
            if (task.getId() == taskPriority.getId()) {
                continue;
            }

            LocalDateTime existStartTime = taskPriority.getStartTime();
            if (existStartTime == null) {
                return;
            }

            LocalDateTime existEndTime = taskPriority.getEndTime();


                if (!startTime.isBefore(existEndTime)) {
                continue;
                }if (!existStartTime.isBefore(endTime)) {
                continue;
                }
                throw new TaskValidationException("Задача пересекается с другой задачей: " + taskPriority);


        }
    }

    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        int duration = 0;

        for (int subtaskId : epic.getSubtasksIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                LocalDateTime subtaskStartTime = subtask.getStartTime();
                LocalDateTime subtaskEndTime = subtask.getEndTime();

                if (subtaskStartTime != null && (epicStartTime == null || subtaskStartTime.isBefore(epicStartTime))) {
                    epicStartTime = subtaskStartTime;
                }
                if (subtaskEndTime != null && (epicEndTime == null || subtaskEndTime.isAfter(epicEndTime))) {
                    epicEndTime = subtaskEndTime;
                }
                duration += subtask.getDuration();
            }
        }
        epic.setStartTime(epicStartTime);
        epic.setEndTime(epicEndTime);
        epic.setDuration(duration);

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

    public int getGeneratedIds() {
        return generatedIds;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
