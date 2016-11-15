package de.ilume.western.union.extractor.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import de.ilume.western.union.extractor.util.LoggerUtil;

/**
 * 
 * @author Benedikt Sixt
 *
 */
public class Configuration {
	private static final Logger log = LoggerUtil.getLogger(Configuration.class);

	public static final String PARAMATER_SEARCH_WORD = "suchbegriff";
	
	public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
	
	private Boolean printProperties;
	private String sourceUrl;
	private String listCssQuery;
	private String sinkFile;
	private Boolean sinkHeader;
	private String userAgent;
	private int processLimit;
	private int msgAfterCount = 0;
	
	private String searchWord;
	
	private void showProperties() {
		System.out.println("test: " + printProperties);
		System.out.println("sourceUrl: " + sourceUrl);
		System.out.println("listCssQuery: " + listCssQuery);
		System.out.println("sinkFile: " + sinkFile);
		System.out.println("userAgent: " + userAgent);
	}

	public Configuration(final String[] args) {
		Properties prop = new Properties();
		
		try (InputStream propFile = Configuration.class.getClassLoader().getResourceAsStream("config.properties")) {
			prop.load(propFile);
			
			setProperties(prop);
			setPropertiesFromCmdLine(args);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim laden der Configuration", e);
		}
		

		if (printProperties) {
			showProperties();
		}
	}
	
	private void setPropertiesFromCmdLine(final String[] args) {
		CommandLine cmdLine = parseOptions(buildCommandLineOptions(), args);
		
		searchWord = cmdLine.getOptionValue(PARAMATER_SEARCH_WORD, args[0]);
	}
	
	private void setProperties(final Properties prop) {
		printProperties = Boolean.valueOf(prop.getProperty("western.union.print.properties", "false"));
		sourceUrl = prop.getProperty("western.union.source.url");
		listCssQuery = prop.getProperty("western.union.source.list.css.query");
		sinkFile = prop.getProperty("western.union.sink.file");
		sinkHeader = Boolean.valueOf(prop.getProperty("western.union.sink.header", "false"));
		userAgent = prop.getProperty("western.union.user.agent", USER_AGENT);
		processLimit = Integer.valueOf(prop.getProperty("western.union.process.limit", "-1"));
		msgAfterCount = Integer.valueOf(prop.getProperty("western.union.msg.after.count", "0"));
	}

	public Boolean getPrintProperties() {
		return printProperties;
	}

	public void setPrintProperties(Boolean printProperties) {
		this.printProperties = printProperties;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public String getListCssQuery() {
		return listCssQuery;
	}

	public void setListCssQuery(String listCssQuery) {
		this.listCssQuery = listCssQuery;
	}

	public String getSinkFile() {
		return sinkFile;
	}

	public void setSinkFile(String sinkFile) {
		this.sinkFile = sinkFile;
	}
	
	public Boolean getSinkHeader() {
		return sinkHeader;
	}

	public void setSinkHeader(Boolean sinkHeader) {
		this.sinkHeader = sinkHeader;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	public int getProcessLimit() {
		return processLimit;
	}

	public void setProcessLimit(int processLimit) {
		this.processLimit = processLimit;
	}

	public int getMsgAfterCount() {
		return msgAfterCount;
	}

	public void setMsgAfterCount(int msgAfterCount) {
		this.msgAfterCount = msgAfterCount;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	private CommandLine parseOptions(final Options options, final String[] args) {
		try {
			return new BasicParser().parse(options, args);
		} catch (ParseException e) {
			log.log(Level.SEVERE, "Fehler beim parsen der übergebenen Parameter an das Program.", e);
			return null;
		}
	}

	private Options buildCommandLineOptions() {
		Options options = new Options();
		options.addOption(PARAMATER_SEARCH_WORD, true, "Suchbegriff für die initiale Suche");
		return options;
	}
}
