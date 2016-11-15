package de.ilume.western.union.extractor;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ilume.western.union.constants.Constants;
import de.ilume.western.union.extractor.config.Configuration;
import de.ilume.western.union.extractor.data.FailedDataHandler;
import de.ilume.western.union.extractor.extractor.DetailExtractor;
import de.ilume.western.union.extractor.extractor.SearchResultExtractor;
import de.ilume.western.union.extractor.printer.CsvPrinter;
import de.ilume.western.union.extractor.util.LoggerUtil;

/**
 * 
 * @author Benedikt Sixt, Dominic Recktenwald
 *
 */
public class PRAAdressDetailsExtractor {

	private static final Logger log = LoggerUtil.getLogger(PRAAdressDetailsExtractor.class);

	// private static String destinationPath = new
	// File(System.getProperty("java.class.path")).getAbsoluteFile().getParentFile().toString();
	private static PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();

	/**
	 * Starting point of the programm. We iterate over each given argument. We
	 * choose between a valid URL and search terms.
	 * 
	 * @param args - all given parameter
	 *            
	 */
	public static void main(String[] args) {
		
		/*
		 * Itarate over each given parameter
		 */
		for (int i = 0; i < args.length; i++) {
			try 
			{
				/*
				 * Try to form a URL object from the current parameter.
				 * If it is not a valid URL, an MalformedURLException is thrown.
				 */
				URL searchURL = new URL(args[i]);	

				prepareURLRun(searchURL.toString(), true);
			} 
			catch (MalformedURLException ulre) 
			{
				System.out.println("Sammele Daten für den übergebenen Suchbegriff: " + args[i]);
				
				String[] searchTerms = new String[] { "-" + Configuration.PARAMATER_SEARCH_WORD, args[i] };		//Set the current search term. TODO Why is this an array

				/*
				 * Build the configuration object
				 * TODO Maybe we could use one global Configuration object?
				 */
				Configuration config = new Configuration(searchTerms);
				config.setSinkFile(Constants.csvDestinationPath + "/CBI-AddressDetails.csv");
				config.setSinkHeader(true);
				config.setPrintProperties(true);
				config.setProcessLimit(-1);
				config.setMsgAfterCount(2);

				
				extractor.run(config);		//Start collecting data and building the csv files using a search term

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//At this point we we are mostly done. 
		ArrayList<String> failedUrls = new ArrayList<String>();
		failedUrls.addAll(FailedDataHandler.failedUrls);
		
		FailedDataHandler.failedUrls.clear();
		
		if(failedUrls.size() > 0)
		{
			System.out.println("Mindestens ein Datensatz ist fehlgeschlagen. Prüfe erneut diese erneut.");
			handleURLRun(failedUrls);
		}
	}
	
	private static void handleURLRun(ArrayList<String> allLinks)
	{
		int finishedResults = 0;
		
		/*
		 * Iterate over each entry
		 */
		for (String s : allLinks) {
			
			/*
			 * Use the current url as a search term for the configuration.
			 * TODO We dont need this. But the searchTerms currently cant be null
			 */
			String[] searchTerms = new String[] { "-" + Configuration.PARAMATER_SEARCH_WORD, s };

			/*
			 * Build the configuration object.
			 * TODO Maybe we can use a global configuration object
			 */
			Configuration currentConf = new Configuration(searchTerms);
			currentConf.setSinkFile(Constants.csvDestinationPath + "/CBI-AddressDetails.csv");
			currentConf.setSinkHeader(true);
			currentConf.setPrintProperties(true);

			extractor.runByUrl(currentConf, s);		//Build the data by the url
			
			finishedResults++;
			
			/*
			 * Print an output for each 25 finished results
			 */
			if(finishedResults % 25 == 0 || finishedResults == allLinks.size() - 1)
			{
				System.out.println(finishedResults + " von " + allLinks.size() + " verarbeitet");
			}
		}
	} 

	/**
	 * We use this function to collect all URLs inside the "Appointed representatives / tied agents / PSD or EMD agents"
	 * section of a site. If we found at least one entry in this section, we will collect the data of this entry
	 * and add it to our .csv files
	 * 
	 * @param url - the url of the site to check
	 * @throws IOException
	 */
	private static void prepareURLRun(String url, boolean calledAtStart) throws IOException {
		
		System.out.println("Sammele Datensätze für die übergebene URL: " + url);
		
		FcaExtractor fcae = new FcaExtractor();		//FcaExtractor object

		/*
		 * The list of the found urls inside the Appointed representatives / tied agents / PSD or EMD agents
		 * section.
		 */
		ArrayList<String> allLinks = (ArrayList<String>) fcae.getLinkList(url);
		
		/*
		 * We also have to add the search url to the list
		 */
		if(calledAtStart)
			allLinks.add(0, url);
		

		/*
		 * Check if we have at least one url found
		 */
		if (allLinks.size() > 0) {
			System.out.println(allLinks.size() + " Datensätze gefunden");
			System.out.println("Detaildaten werden nun abgefragt");
			handleURLRun(allLinks);
			
//			System.out.println("Detaildaten werden nun abgefragt");
//			/*
//			 * Iterate over each entry
//			 */
//			for (String s : allLinks) {
//				
//				/*
//				 * Use the current url as a search term for the configuration.
//				 * TODO We dont need this. But the searchTerms currently cant be null
//				 */
//				String[] searchTerms = new String[] { "-" + Configuration.PARAMATER_SEARCH_WORD, s };
//
//				/*
//				 * Build the configuration object.
//				 * TODO Maybe we can use a global configuration object
//				 */
//				Configuration currentConf = new Configuration(searchTerms);
//				currentConf.setSinkFile(Constants.csvDestinationPath + "/CBI-AddressDetails.csv");
//				currentConf.setSinkHeader(true);
//				currentConf.setPrintProperties(true);
//
//				extractor.runByUrl(currentConf, s);		//Build the data by the url
//				
//				finishedResults++;
//				
//				/*
//				 * Print an output for each 25 finished results
//				 */
//				if(finishedResults % 25 == 0 || finishedResults == allLinks.size() - 1)
//				{
//					System.out.println(finishedResults + " von " + allLinks.size() + " verarbeitet");
//				}
//			}
		} 
		else {
			System.out.println("Keine Datensätze unter der URL: " + url + " gefunden");
		}
	}

	/**
	 * This function will initiate the collecting of the data and building the .csv files,
	 * by using a searchTerm. We have to execute this function one time max.
	 * 
	 * @param config - the configuration with which we collect the data
	 */
	public void run(final Configuration config) {

		SearchResultExtractor s = null;		//SearchResultExtractor object

		/*
		 * Create a new CsvPrinter object. There we will build our .csv files
		 */
		try (CsvPrinter printer = new CsvPrinter(config)) {

			s = new SearchResultExtractor(config);							//Instantiate SearchResultExtractor object
			
			/*
			 * Call the forEach function of the SearchResultExtractor class.
			 * This will build and update the .csv files
			 */
			s.forEach(new DetailExtractor(config).withPrinter(printer));

		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Öffnen der CSV Datei '" + config.getSinkFile() + "'", e);
		}

//		log.log(Level.INFO, "Bearbeitung abgeschlossen");

		/*
		 * Handle the entries we found inside the search
		 */
		handleSerachTermUrls(s);
	}
	
	/**
	 * This function will handle all urls, we found by using a search term as a parameter, starting the
	 * programm. For each url, we will call the prepareUrl run function, to check if the url contains 
	 * Appointed representatives / tied agents / PSD or EMD agents
	 * 
	 * @param searchResultExtractor - the responsible searchResultExtractor
	 */
	private void handleSerachTermUrls(SearchResultExtractor searchResultExtractor)
	{
		System.out.println("Bearbeitung der einzelnen Suchergebnisse abgeschlossen");
		System.out.println("Prüfe die einzelnen Suchergebnisse nach \"Appointed representatives / tied agents / PSD or EMD agents\"");
		
		/*
		 * Check if we at least found one sub url
		 */
		if (searchResultExtractor != null && searchResultExtractor.foundSubLinks.size() > 0) {
			try {
				for (String url : searchResultExtractor.foundSubLinks) 
				{
					PRAAdressDetailsExtractor.prepareURLRun(url, false);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Within this function, we collect and create all the data by using directly an
	 * URL, not an search term
	 * 
	 * @param config - the needed configuration
	 * @param url - the url to check
	 */
	private void runByUrl(final Configuration config, String url) {
		
		/*
		 * Create a new printer object
		 */
		try (CsvPrinter printer = new CsvPrinter(config)) {
			SearchResultExtractor s = new SearchResultExtractor(config);
			s.buildSingleDataResult(new DetailExtractor(config).withPrinter(printer), url);		
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim Öffnen der CSV Datei '" + config.getSinkFile() + "'", e);
		}
	}
}
