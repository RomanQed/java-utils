package com.github.romanqed.jutils.structs;

public interface Link {
    <T extends Link> void attach(T tail);

    Link detach();

    Link tail();
}
