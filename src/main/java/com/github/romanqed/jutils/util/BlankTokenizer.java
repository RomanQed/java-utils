package com.github.romanqed.jutils.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Pattern;

public class BlankTokenizer implements Tokenizer {
    private static final Pattern PATTERN = Pattern.compile("\\s+");

    @Override
    public Collection<String> tokenize(String string) {
        return Arrays.asList(PATTERN.split(string));
    }
}
