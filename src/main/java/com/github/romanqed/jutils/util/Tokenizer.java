package com.github.romanqed.jutils.util;

import java.text.ParseException;
import java.util.List;

public interface Tokenizer {
    List<String> tokenize(String string) throws ParseException;
}
