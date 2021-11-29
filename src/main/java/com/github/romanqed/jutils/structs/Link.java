package com.github.romanqed.jutils.structs;

public interface Link {
    void attach(Link tail);

    Link detach();

    Link tail();
}
