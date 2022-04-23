package com.github.romanqed.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Interface describing the task factory.
 */
public interface TaskFactory {
    /**
     * Creates a task by binding it to the factory executor.
     *
     * @param action the action on the basis of which the task will be created
     * @param <T>    type of the task's return value
     * @return {@link Task} instance
     */
    <T> Task<T> createTask(Callable<T> action);

    /**
     * @return {@link ExecutorService} instance bounded to this {@link Task}
     */
    ExecutorService getExecutor();

    /**
     * @return true if it has an executor, and false if it is null
     */
    boolean hasExecutor();

    /**
     * Stops the factory.
     */
    void close();
}
