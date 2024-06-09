package ru.kedroff.project.managers.historyManager;

import ru.kedroff.project.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    public Node head = null;
    public Node tail = null;
    private int size = 0;
    private final Map<Integer, Node> customLinkedList = new HashMap<>();

    @Override
    public void add(Task task) {
        if (customLinkedList.containsKey(task.getID())) {
            removeNode(customLinkedList.get(task.getID()));
            size -= 1;
        } else {
            customLinkedList.put(task.getID(), new Node(task));
        }
        linkLast(customLinkedList.get(task.getID()));
        size += 1;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        if (customLinkedList.containsKey(id)) {
            removeNode(customLinkedList.get(id));
            customLinkedList.remove(id);
            size -= 1;
        }
    }

    private void linkLast(Node node) {
        if (head == null) {
            head = node;
            tail = head;
        } else {
            tail.setPrev(node);
            node.setNext(tail);
            tail = node;
            node.setPrev(null);
        }
    }

    private ArrayList<Task> getTasks() {
        if (size == 0) {
            return null;
        }
        ArrayList<Task> taskHistory = new ArrayList<>();
        Node firstElement = tail;
        taskHistory.add(firstElement.getData());
        if (size == 1) {
            return taskHistory;
        }
        for (int i = 0; i < size - 1; i++) {
            firstElement = firstElement.getNext();
            taskHistory.add(firstElement.getData());
        }
        return taskHistory;
    }

    private void removeNode(Node node) {
        if (head == node && tail == node) {
            head = null;
            tail = null;
        } else if (head == node) {
            node.getPrev().setNext(null);
            head = node.getPrev();
        } else if (tail == node) {
            node.getNext().setPrev(null);
            tail = node.getNext();
        } else {
            node.getNext().setPrev(node.getPrev());
            node.getPrev().setNext(node.getNext());
        }
    }

    public static class Node {
        private Task data;
        private Node next;
        private Node prev;

        public Node(Task data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }

        public Task getData() {
            return data;
        }

        public void setData(Task data) {
            this.data = data;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public Node getPrev() {
            return prev;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }
    }
}
