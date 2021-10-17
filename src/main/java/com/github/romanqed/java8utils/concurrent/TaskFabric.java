package com.github.romanqed.java8utils.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public interface TaskFabric {
    <T> Task<T> createTask(Callable<T> action);

    ExecutorService getExecutor();

    boolean isQueue();
}
