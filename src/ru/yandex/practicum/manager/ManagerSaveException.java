package ru.yandex.practicum.manager;

import java.io.IOException;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}

