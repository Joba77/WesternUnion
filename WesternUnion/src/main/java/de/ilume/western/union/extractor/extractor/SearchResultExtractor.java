package de.ilume.western.union.extractor.extractor;

import static de.ilume.western.union.extractor.util.XmlUtil.getText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.ilume.western.union.extractor.config.Configuration;
import de.ilume.western.union.extractor.data.AddressData;
import de.ilume.western.union.extractor.data.PrincipalData;
import de.ilume.western.union.extractor.util.LoggerUtil;

/**
 * 
 * @author Benedikt Sixt, Dominic Recktenwald
 *
 */
public class SearchResultExtractor {
	private static final Logger log = LoggerUtil.getLogger(SearchResultExtractor.class);
	
	public static final String EL_SEARCH_WORD = "\\[SEARCHWORDS\\]";
		
	private final Configuration config;
	
	public List<String> foundSubLinks;							//All urls, we found after collecting the data by a search term
	
	/**
	 * Constructor
	 * @param config
	 */
	public SearchResultExtractor(final Configuration config) {
		this.config = config;
		foundSubLinks = new ArrayList<String>();
	}
	
	/**
	 * Get the document object by the class global configuration
	 * @return
	 * @throws IOException
	 */
	private Document getListHtmlDocument() throws IOException {
		/*
		 * Build the search url with the class global Configuration.
		 * With this url, we get the document object
		 */
		Document doc = Jsoup.connect(buildSearchUrl(config))
				.userAgent(config.getUserAgent())
				.get();
		return doc;
	}
	
	/**
	 * 
	 * @param config - the given Configuration obejt. Is needed to build the url
	 * @return
	 */
	private String buildSearchUrl(final Configuration config) {
		String url = config.getSourceUrl();
		
		url = url.replaceAll(EL_SEARCH_WORD, config.getSearchWord());
		
		return url;
	}
	
	/**
	 * Within this function, we will iterate over all search result, after starting the programm
	 * with a search term. We collect the data of each search result. 
	 * We will add each found results url to the foundSubLinks list.
	 * 
	 * @param detail
	 */
	public void forEach(final IDetailExtractor detail) {
		List<Element> linkList;
		try {
			Document doc = getListHtmlDocument();				//Dom of the current serach result
			linkList = doc.select(config.getListCssQuery());	//Get all search result links
			
			if (linkList.size() > 0) {
				if (config.getProcessLimit() > 0) {
					linkList = linkList.subList(0, config.getProcessLimit());
				}
				int msgAfterCount = config.getMsgAfterCount();
				
				int singleProcessLeft = msgAfterCount;
				int listSize = linkList.size();					//The size of the linklist
				
				System.out.println(listSize + " Suchergebnisse gefunden");
				
				/*
				 * Iterate over the linklist 
				 */
				for (int i = 0; i < listSize; i++) {
					
					Element linkTag = linkList.get(i);			//Get the current linktag

					Element listData = getListData(linkTag);					//Get the current listdata
					AddressData data = new AddressData();						//New AddressData
					data.setName(getName(linkTag));				
					data.setReferenceNumber(getReferenceNumber(listData));		//Set the name of the address data
					data.setLink(linkTag.attr("href"));							//Set the link of the address data
					data.setTradingBrandName(getTradingOrBrandNames(listData));	//Set the trading brand name of the address data
					data.setBusinessType(getBusinessType(listData));			//Set businesstype of the address data
					data.setStatus(getStatus(listData));						//Set status of the address data
					
					detail.detailsFor(data, new PrincipalData());				//Execute the detailsFor function
					
					foundSubLinks.add(linkTag.attr("href"));					//Add the link to the subLinks list
					
					singleProcessLeft--;
					if (msgAfterCount > 0 && singleProcessLeft <= 0) {
						System.out.println((i + 1) + " Suchergebnisse verarbeitet");
						singleProcessLeft = msgAfterCount;
					}
				}
				
			} else {
				
				buildSingleDataResult(detail, getDetailLink(doc));				//We only found a single result, so this must be a detail page
			}
		} catch (IOException e) {
			log.log(Level.SEVERE, "Fehler beim auslesen der Liste.", e);
		}
	}
	
	/**
	 * Inside this function, we build the link of the detail page
	 * @param doc
	 * @return
	 */
	private String getDetailLink(final Document doc) {
		String detailLink = "";
		if (doc.toString().contains("window.location.href")) {
			Elements scripts = doc.select("script");
			for (Element script: scripts) {
				String data = script.data();
				if (data.toString().contains("window.location.href")) {
					Matcher redirectUrlMatcher = Pattern.compile(".*?window\\.location\\.href\\s*=\\s*['|\"]([\\w\\.:\\/\\?=\\&\\+\\%]+)['|\"].*?", 
							Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CHARACTER_CLASS).matcher(data);

					if (redirectUrlMatcher.matches() && redirectUrlMatcher.groupCount() > 0) {
						detailLink = redirectUrlMatcher.group(1);
					}
				}
			}
		}
		return detailLink;
	}
	
	/**
	 * Within this helper function, we build the result of a single detail page
	 * 
	 * @param detail
	 * @param url
	 */
	public void buildSingleDataResult(final IDetailExtractor detail, String url)
	{
		System.out.print(".");
		AddressData data = new AddressData();
		
		data.setLink(url);
		
		detail.detailsFor(data, new PrincipalData());
	}
	
	/*
	 * Some getter functions
	 */
	private String getName(final Element linkTag) {
		return linkTag.text();
	}
	
	private String getTradingOrBrandNames(final Element listData) {
		return getText(listData.child(1));
	}
	
	private String getBusinessType(final Element listData) {
		return getText(listData.child(2));
	}
	
	private String getReferenceNumber(final Element listData) {
		return getText(listData.child(3));
	}
	
	private String getStatus(final Element listData) {
		String result = getText(listData.child(4));
		if (result == null) {
			result = "";
		}
		return result;
	}
	
	public Element getListData(final Element linkTag) {
		Element trTag = linkTag;
		
		while (!trTag.tagName().equals("tr") && trTag.parent() != null) {
			trTag = trTag.parent();
		}
		
		return trTag;
	}
}
