package com.github.romanqed.jutils.util;

public interface Link {
    <T extends Link> void attach(T tail);

    Link detach();

    Link tail();
}
