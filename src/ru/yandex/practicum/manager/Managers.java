package ru.yandex.practicum.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.http.HttpTaskManager;
import ru.yandex.practicum.server.KVServer;

import java.io.IOException;

public class Managers {

    private Managers(){}
    public static TaskManager getDefault(){
        return new HttpTaskManager(KVServer.PORT);
    }
    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.create();
    }

    public static KVServer getDefaultKVServer() throws IOException{
        final KVServer kvserver = new KVServer();
        kvserver.start();
        return kvserver;
    }
}
