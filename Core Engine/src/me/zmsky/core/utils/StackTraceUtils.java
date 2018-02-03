package me.zmsky.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public abstract class StackTraceUtils {
	public static String getStackTrace(Throwable e){
		Writer w = new StringWriter();
		PrintWriter printWriter = new PrintWriter(w);
		e.printStackTrace(printWriter);
		return w.toString();
	}
}
