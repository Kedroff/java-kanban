package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node> historyHash = new HashMap<>();
    private Node head = null;
    private Node tail = null;


    @Override
    public void add(Task task) {

        if (task == null) {
            return;
        }

        int id = task.getId();
        Node node = historyHash.get(id);
        if (node != null) {
            remove(id);
        }

        linkLast(task);
        historyHash.put(id, tail);
    }

    @Override
    public void remove(int id) {
        Node node = historyHash.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    public void removeNode(Node node) {
        if (node.prevIndex != null) {
            node.prevIndex.nextIndex = node.nextIndex;
            if (node.nextIndex == null) {
                tail = node.prevIndex;
            } else {
                node.nextIndex.prevIndex = node.prevIndex;
            }
        } else {
            head = node.nextIndex;
            if (head == null) {
                tail = null;
            } else {
                head.prevIndex = null;
            }
        }
    }

    public void linkLast(Task element) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.nextIndex = newNode;
        }
    }

    public List<Task> getTasks() {
        List<Task> list = new ArrayList<>();

        Node node = head;

        while (node != null) {
            list.add((Task) node.data);
            node = node.nextIndex;
        }
        return list;
    }

    @Override
    public void removeAllTasks() {
        removeAllObjectsOfType(Task.class);
    }

    @Override
    public void removeAllSubtasks() {
        removeAllObjectsOfType(Subtask.class);
    }

    @Override
    public void removeAllEpics() {
        removeAllObjectsOfType(Epic.class);
    }

    private <T> void removeAllObjectsOfType(Class<T> objectType) {
        Node node = head;
        while (node != null) {
            if (objectType.isInstance(node.data)) {
                Node nextNode = node.nextIndex;
                removeNode(node);
                node = nextNode;
            } else {
                node = node.nextIndex;
            }
        }
    }

}



