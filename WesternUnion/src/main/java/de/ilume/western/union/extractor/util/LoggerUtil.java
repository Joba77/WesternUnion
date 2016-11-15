package de.ilume.western.union.extractor.util;

import java.util.logging.Logger;

public class LoggerUtil {
	public static Logger getLogger(final Class<?> classToLogger) {
		return Logger.getLogger(classToLogger.getName());
	}
}
