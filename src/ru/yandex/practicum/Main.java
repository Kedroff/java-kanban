package ru.yandex.practicum;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.yandex.practicum.managers.Managers;
import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;
import ru.yandex.practicum.utils.Utils;

import java.time.LocalDateTime;

import static java.time.Month.*;

public class Main {
    public static void main(String[] args) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new Utils.LocalDateTimeAdapter())
                .create();
        Task task1 = new Task("Read book every day", "30 pages",
                LocalDateTime.of(2024, MARCH,28,13, 0), 60);
        Managers.getDefault().addNewTask(task1);
        Task task2 = new Task("jump every day", "30 iterations",
                LocalDateTime.of(2024, APRIL,28,13, 0), 60);
        Managers.getDefault().addNewTask(task2);
        Epic epic1 = new Epic("Съездить в Москву", "обязательно до лета");
        Managers.getDefault().addNewEpic(epic1);
        Subtask subtask11 = new Subtask("купить билеты", "дешёвые билеты", epic1.getID(),
                LocalDateTime.of(2023, AUGUST,28,13, 0), 60);
        Managers.getDefault().addNewSubtask(subtask11);
        Subtask subtask12 = new Subtask("купить одежду", "крутую одежду", epic1.getID(),
                LocalDateTime.of(2023, SEPTEMBER,28,13, 0), 60);
        Managers.getDefault().addNewSubtask(subtask12);
        Epic epic2 = new Epic("посмотреть кино", "обязательно до конца месяца");
        Managers.getDefault().addNewEpic(epic2);
        Subtask subtask21 = new Subtask("найти кино", "в хорошем качестве", epic2.getID(),
                LocalDateTime.of(2023, DECEMBER,28,13, 0), 60);
        Managers.getDefault().addNewSubtask(subtask21);

        System.out.println(gson.toJson(task1));
        System.out.println(gson.toJson(task2));
        System.out.println(gson.toJson(epic1));
        System.out.println(gson.toJson(subtask11));
        System.out.println(gson.toJson(subtask12));
        System.out.println(gson.toJson(epic2));
        System.out.println(gson.toJson(subtask21));

        String jsonString = gson.toJson(subtask11);
        Subtask subtaskFromJson = gson.fromJson(jsonString, Subtask.class);
        System.out.println(subtaskFromJson);


    }
}