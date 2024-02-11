package ru.yandex.practicum.http;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.manager.FileBackedTasksManager;
import ru.yandex.practicum.manager.Managers;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.tasks.TaskType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson;
    private final KVTaskClient client;

    public HttpTaskManager(int port){
        this(port,false);
    }

    public HttpTaskManager(int port,boolean load){
        super(null);
        gson = Managers.getGson();
        client = new KVTaskClient(port);
        if (load){
            load();
        }
    }

    protected void addTasks(List<? extends Task> tasks){
        for (Task task : tasks){
            final int id = task.getId();
            if (id > generatedIds){
                generatedIds = id;
            }
            TaskType type = task.getType();
            if (type == TaskType.TASK){
                this.tasks.put(id,task);
                prioritizedTasks.add(task);
            }else if (type == TaskType.SUBTASK){
                subtasks.put(id, (Subtask) task);
                prioritizedTasks.add(task);
            }else if (type == TaskType.EPIC){
                epics.put(id,(Epic) task);
            }
        }
    }
    private void load(){
        ArrayList<Task> tasks = gson.fromJson(client.load("tasks"),new TypeToken<ArrayList<Task>>() {
        }.getType());
        addTasks(tasks);

        ArrayList<Epic> epics = gson.fromJson(client.load("epics"),new TypeToken<ArrayList<Epic>>() {
        }.getType());
        addTasks(epics);

        ArrayList<Subtask> subtasks = gson.fromJson(client.load("subtasks"),new TypeToken<ArrayList<Subtask>>() {
        }.getType());
        addTasks(subtasks);

        List<Integer> history = gson.fromJson(client.load("history"),new TypeToken<ArrayList<Integer>>() {
        }.getType());

        for (Integer taskId : history){
            historyManager.add(getTaskByIdentify(taskId));
        }
    }
    @Override
    public void save(){
        String jsonTasks = gson.toJson(new ArrayList<>(tasks.values()));
        client.put("tasks",jsonTasks);

        String jsonSubtasks = gson.toJson(new ArrayList<>(subtasks.values()));
        client.put("subtasks",jsonSubtasks);

        String jsonEpics = gson.toJson(new ArrayList<>(epics.values()));
        client.put("epics",jsonEpics);

        String jsonHistory = gson.toJson(historyManager.getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        client.put("history",jsonHistory);
    }
}
