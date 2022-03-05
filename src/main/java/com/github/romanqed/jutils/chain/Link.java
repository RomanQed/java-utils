package com.github.romanqed.jutils.chain;

/**
 * An interface describing a structure that can be unidirectional linked.
 */
public interface Link {
    /**
     * Attaches a link instance to the current object.
     *
     * @param tail link object
     * @param <T>  type of link to be attached
     */
    <T extends Link> void attach(T tail);

    /**
     * Detaches the attached link, if nothing is attached, null will be returned.
     *
     * @return detached {@link Link}
     */
    Link detach();

    /**
     * Returns the currently attached link, if nothing is attached, returns null.
     *
     * @return attached link
     */
    Link tail();
}
