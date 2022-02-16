package com.github.romanqed.jutils.chain;

public interface Link {
    <T extends Link> void attach(T tail);

    Link detach();

    Link tail();
}
