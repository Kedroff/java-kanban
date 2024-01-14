package ru.yandex.practicum.manager;

public class Node<Task> {
    protected Node <Task> prevIndex;
    protected Task data;
    protected Node <Task> nextIndex;

    public Node(Node prevIndex, Task data, Node nextIndex) {
        this.prevIndex = prevIndex;
        this.data = data;
        this.nextIndex = nextIndex;
    }
}
