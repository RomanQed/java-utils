package com.github.romanqed.jutils.pipeline;

import com.github.romanqed.jutils.util.AbstractLink;
import com.github.romanqed.jutils.util.Action;

import java.util.Objects;

public class ActionLink extends AbstractLink<ActionLink> {
    private final Action<Object, Object> body;
    private final String name;

    @SuppressWarnings("unchecked")
    public ActionLink(String name, Action<?, ?> body) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(body);
        this.name = name;
        this.body = (Action<Object, Object>) body;
    }

    public String getName() {
        return name;
    }

    public ActionLink duplicate() {
        return new ActionLink(name, body);
    }

    public Action<Object, Object> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "{this(" + name + ")} -> " + "{" + (tail() == null ? null : tail().name) + "}";
    }
}
