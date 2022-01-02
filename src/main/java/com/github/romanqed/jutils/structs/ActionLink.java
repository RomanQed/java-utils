package com.github.romanqed.jutils.structs;

import java.util.Objects;
import java.util.function.Function;

public class ActionLink extends AbstractLink<ActionLink> {
    private final Function<Object, Object> body;
    private final String name;

    @SuppressWarnings("unchecked")
    public ActionLink(String name, Function<?, ?> body) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(body);
        this.name = name;
        this.body = (Function<Object, Object>) body;
    }

    public String getName() {
        return name;
    }

    public ActionLink duplicate() {
        return new ActionLink(name, body);
    }

    public Function<Object, Object> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "{this(" + name + ")} -> " + "{" + (tail() == null ? null : tail().name) + "}";
    }
}
