package ru.yandex.practicum.manager;

public class Node<Task> {
    protected Node <Task> prevIndex;
    protected Task data;
    protected Node <Task> nextIndex;

    public Node(Node<Task> prevIndex, Task data, Node<Task> nextIndex) {
        this.prevIndex = prevIndex;
        this.data = data;
        this.nextIndex = nextIndex;
    }
}
