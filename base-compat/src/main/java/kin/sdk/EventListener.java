package kin.sdk;

public interface EventListener<T> {
    void onEvent(T data);
}
