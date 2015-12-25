package com.heaven7.plugin.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by heaven7 on 2015/12/24.
 */
public class Util {

    public static String toString(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        t.printStackTrace(pw);
        Throwable cause = t.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.flush();
        String data = sw.toString();
        pw.close();
        return data;
    }
}
