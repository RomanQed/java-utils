package com.github.romanqed.jutils.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuoteTokenizer implements Tokenizer {
    private static final String REGEX = "%q([^%q]+)%q|\\S+";
    private final Pattern pattern;

    private final char quote;

    public QuoteTokenizer(char quote) {
        this.quote = quote;
        String regex = REGEX.replace("%q", "" + quote);
        pattern = Pattern.compile(regex);
    }

    public QuoteTokenizer() {
        this('"');
    }

    @Override
    public List<String> tokenize(String string) {
        Matcher matcher = pattern.matcher(string);
        List<String> ret = new ArrayList<>();
        while (matcher.find()) {
            ret.add(matcher.group().replace("" + quote, ""));
        }
        return ret;
    }
}
