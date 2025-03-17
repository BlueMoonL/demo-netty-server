package com.demo.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public abstract class LogHelper {
	public static String getPrintStackTrace(Throwable cause) {

		StringWriter errors = new StringWriter();
		cause.printStackTrace(new PrintWriter(errors));

		return errors.toString();
	}

	public static String getPrintStackTrace(Exception e) {

		StringWriter errors = new StringWriter();
		e.printStackTrace(new PrintWriter(errors));
		return errors.toString();
	}
}
