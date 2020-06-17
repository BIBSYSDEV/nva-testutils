package no.unit.nva.testutils;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class ExceptionUtils {

    public static String stackTrace(Exception e){
        StringWriter stringWriter= new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter, true);
        e.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
