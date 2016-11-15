package de.ilume.western.union.extractor.data;

import java.util.ArrayList;

public class FailedDataHandler {

	public static ArrayList<String> failedUrls = new ArrayList<String>();
	
	public static void addFailedUrl(String url)
	{
		failedUrls.add(url);
	}

}
