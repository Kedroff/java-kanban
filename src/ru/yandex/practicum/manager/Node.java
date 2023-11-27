package ru.yandex.practicum.manager;

public class Node<Task> {
    protected Node prevIndex;
    protected Task data;
    protected Node nextIndex;

    public Node(Node prevIndex, Task data, Node nextIndex) {
        this.prevIndex = prevIndex;
        this.data = data;
        this.nextIndex = nextIndex;
    }
}
