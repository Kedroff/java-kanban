package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    List<Task> historyList = new ArrayList<>();
    @Override
    public void add(Task task){
        historyList.add(task);
    }
    @Override
    public List<Task> getHistory() {
        while (historyList.size() > 10) {
            historyList.remove(0);
        }
        return historyList;
    }
}
