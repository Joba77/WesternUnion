package de.ilume.western.union.constants;

import java.io.File;

public class Constants {

	//public static String csvDestinationPath = "C:/Users/drecktenwald/Desktop";// --> For testing
	public static String csvDestinationPath = new File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile().toString();
}
