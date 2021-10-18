package com.github.romanqed.jutils.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

public interface TaskFabric {
    <T> Task<T> createTask(Callable<T> action);

    ExecutorService getExecutor();

    boolean hasExecutor();
}
