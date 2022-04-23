package com.github.romanqed.util;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A tokenizer that splits a string by spaces.
 */
public class BlankTokenizer implements Tokenizer {
    private static final Pattern PATTERN = Pattern.compile("\\s+");

    @Override
    public List<String> tokenize(String string) {
        return Arrays.asList(PATTERN.split(string));
    }
}
