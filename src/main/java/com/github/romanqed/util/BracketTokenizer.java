package com.github.romanqed.util;

import java.text.ParseException;
import java.util.*;

/**
 * <p>A tokenizer that splits a string according to the brackets rule.</p>
 * <p>The closing and opening brackets can be any 2 different characters.</p>
 */
public class BracketTokenizer implements Tokenizer {
    private final char open;
    private final char close;
    private final Set<Character> blank;

    public BracketTokenizer(char open, char close, String blank) {
        if (open == close) {
            throw new IllegalArgumentException("Open token can't be equal to close token");
        }
        this.open = open;
        this.close = close;
        if (blank.isEmpty()) {
            throw new IllegalArgumentException("String contains \"blank\" symbols can't be empty");
        }
        Set<Character> blankSet = new HashSet<>();
        for (int i = 0; i < blank.length(); ++i) {
            blankSet.add(blank.charAt(i));
        }
        this.blank = Collections.unmodifiableSet(blankSet);
    }

    public BracketTokenizer(char open, char close) {
        this(open, close, " ");
    }

    public BracketTokenizer() {
        this('(', ')', " ");
    }

    @Override
    public List<String> tokenize(String string) throws ParseException {
        List<String> ret = new ArrayList<>();
        String source = string.trim() + " ";
        int count = 0;
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < source.length(); ++i) {
            char cur = source.charAt(i);
            if (cur == open) {
                count++;
                if (count > 1) {
                    temp.append(cur);
                }
                continue;
            }
            if (cur == close) {
                if (count < 1) {
                    throw new ParseException("Invalid close bracket " + close, i);
                }
                if (count == 1) {
                    ret.add(temp.toString());
                    temp = new StringBuilder();
                    count = 0;
                } else {
                    temp.append(cur);
                    count--;
                }
                continue;
            }
            if (blank.contains(cur) && count == 0) {
                if (temp.length() != 0) {
                    ret.add(temp.toString());
                    temp = new StringBuilder();
                }
                continue;
            }
            temp.append(cur);
        }
        if (count != 0) {
            throw new ParseException("Missing bracket " + close, string.length() - 1);
        }
        return ret;
    }
}
