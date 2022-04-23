package com.github.romanqed.util.util;

import java.text.ParseException;
import java.util.List;

/**
 * An interface describing a universal tokenizer.
 */
public interface Tokenizer {
    /**
     * Splits the string by some attribute.
     *
     * @param string string to split
     * @return list of received partitions
     * @throws ParseException if an error occurred during the splitting process
     */
    List<String> tokenize(String string) throws ParseException;
}
