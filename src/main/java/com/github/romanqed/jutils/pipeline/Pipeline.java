package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.Action;

import java.util.Map;

public interface Pipeline extends Action<Object, Object>, Map<String, Action<?, ?>> {
    void insertAfter(String after, String key, Action<?, ?> value);

    void insertBefore(String before, String key, Action<?, ?> value);
}
