package com.github.romanqed.jutils.chain;

/**
 * <p>An interface describing a unidirectional connected structure</p>
 * <p>that supports a set of primitive actions and is available for iteration.</p>
 *
 * @param <E> Generic, specialized type basic type of link.
 */
public interface Chain<E extends Link> extends Iterable<E> {
    /**
     * Adds the link to the end of the chain.
     *
     * @param link {@link Link} to be added
     */
    void add(E link);

    /**
     * Deletes the first link from the end and returns it.
     *
     * @return detached link object
     */
    E remove();

    /**
     * Returns the current link and moves the pointer to the next one.
     *
     * @return the link currently selected
     */
    E next();

    /**
     * Clears the chain.
     */
    void clear();
}
