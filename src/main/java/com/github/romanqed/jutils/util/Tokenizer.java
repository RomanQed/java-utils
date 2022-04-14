package com.github.romanqed.jutils.util;

import java.text.ParseException;
import java.util.Collection;

public interface Tokenizer {
    Collection<String> tokenize(String string) throws ParseException;
}
