package com.github.romanqed.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * A set of simple I/O utilities.
 */
public final class IOUtil {
    /**
     * Reads the input stream using {@link BufferedReader}.
     * Does not close the stream.
     *
     * @param stream  stream to be read
     * @param charset charset used
     * @return a string containing the read data
     */
    public static String readInputStream(InputStream stream, Charset charset) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
        return reader.lines().reduce("", (left, right) -> left + right + "\n");
    }

    /**
     * Reads a resource file.
     *
     * @param name resource file name
     * @return a string containing the read data
     * @throws IOException if an error occurred while accessing the specified file
     */
    public static String readResourceFile(String name) throws IOException {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        InputStream stream = classLoader.getResourceAsStream(name);
        if (stream == null) {
            return "";
        }
        String ret = readInputStream(stream, StandardCharsets.UTF_8);
        stream.close();
        return ret;
    }

    /**
     * Gets the {@link Throwable} stack trace as a string.
     *
     * @param throwable throwable to get a stack trace
     * @return a string containing the result
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter ret = new StringWriter();
        throwable.printStackTrace(new PrintWriter(ret));
        return ret.toString();
    }
}
