package ru.yandex.practicum.manager;

import ru.yandex.practicum.tasks.Epic;
import ru.yandex.practicum.tasks.Subtask;
import ru.yandex.practicum.tasks.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> historyHash = new HashMap<>();
    private Node<Task> head = null;
    private Node<Task> tail = null;


    @Override
    public void add(Task task) {

        if (task == null) {
            return;
        }

        int id = task.getId();
        Node<Task> node = historyHash.get(id);
        if (node != null) {
            remove(id);
        }

        linkLast(task);
        historyHash.put(id, tail);
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyHash.remove(id);
        if (node == null) {
            return;
        }
        removeNode(node);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(Node<Task> node) {
        final Node<Task> prev = node.prevIndex;
        final Node<Task> next = node.nextIndex;

        if (prev != null) {
            prev.nextIndex = next;
            if (next == null) {
                tail = prev;
            } else {
                next.prevIndex = prev;
            }
        } else {
            head = next;
            if (head == null) {
                tail = null;
            } else {
                head.prevIndex = null;
            }
        }
        for (Map.Entry<Integer, Node<Task>> entry : historyHash.entrySet()) {
            if (entry.getValue() == node) {
                historyHash.remove(entry.getKey());
                break;
            }
        }
    }

    private void linkLast(Task element) {
        final Node<Task> newNode = new Node<>(tail, element, null);
        if (tail == null) {
            head = newNode;
        } else {
            tail.nextIndex = newNode;
        }
        tail = newNode;
    }


    private List<Task> getTasks() {
        List<Task> list = new LinkedList<>();

        Node<Task> node = head;

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
        Node<Task> node = head;
        while (node != null) {
            if (objectType.isInstance(node.data)) {
                Node<Task> nextNode = node.nextIndex;
                removeNode(node);  // добавил удаление значений из мапы в конец метода removeNode
                node = nextNode;
            } else {
                node = node.nextIndex;
            }
        }
    }

}



