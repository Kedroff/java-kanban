package ru.yandex.practicum.manager;

public class Managers {

    private Managers(){   // добавил приватный конструктор

    }
    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }
}
