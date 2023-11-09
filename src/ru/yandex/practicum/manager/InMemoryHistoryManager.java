package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    List<Task> historyList = new ArrayList<>();
    private static final int MAX = 10;  // добавил константу

    @Override
    public void add(Task task) {
        if (task == null) {             // добавил проверку на существование задачи
            return;
        }
        historyList.add(task);

        if (historyList.size() > MAX) {
            historyList.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyList);  // возвращаю не ссылку на коллекцию, а копию.
    }
}
