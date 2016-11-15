package de.ilume.western.union.extractor.extractor;

import static de.ilume.western.union.extractor.util.XmlUtil.contains;
import static de.ilume.western.union.extractor.util.XmlUtil.getText;
import static de.ilume.western.union.extractor.util.XmlUtil.textFromChildAt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.ilume.western.union.extractor.config.Configuration;
import de.ilume.western.union.extractor.data.AddressData;
import de.ilume.western.union.extractor.data.FailedDataHandler;
import de.ilume.western.union.extractor.data.PrincipalData;
import de.ilume.western.union.extractor.printer.IPrinter;
import de.ilume.western.union.extractor.util.LoggerUtil;

/**
 * 
 * @author Benedikt Sixt, Dominic Recktenwald
 *
 */
public class DetailExtractor {
	private static final Logger log = LoggerUtil.getLogger(DetailExtractor.class);
	
	private final Configuration config;
	
	public DetailExtractor(final Configuration config) {
		this.config = config;
	}
	
	/**
	 * Inside this function, we implement the detailFor function of the
	 * IDetailExtractor interface.
	 * We will print the data of each found document to the .csv files
	 * 
	 * TODO If an error occures, we have to save this failed url
	 * 
	 * @param printer - the printer with which we will write the data to the csv files
	 * @return
	 */
	public IDetailExtractor withPrinter(final IPrinter printer) {
		return new IDetailExtractor() {
			@Override
			public void detailsFor(final AddressData data, final PrincipalData pData) {

				try {
					Document detailData = Jsoup.connect(data.getLink()).userAgent(config.getUserAgent()).get(); 	//get the current document
					
					extractPrincipalsData(detailData, pData);		//extract the principal data from the document DOM
					
					extractMissingListData(detailData, data);		//extract missing list data from the document DOM
					
					extractAddressDataTo(detailData, data);			//extract address data from the document DOM
					
					extractBasicDetailsDataTo(detailData, data);	//extract basic detail data from the document DOM
					
					checkAuthorisationStatus(detailData, data);		//check the auth status
					
					printer.print(data, pData);
				} catch (IOException e) {
					log.log(Level.SEVERE, "Problem bei der Verbindung zu '" + data.getLink() + "'", e);
					FailedDataHandler.addFailedUrl(data.getLink());
				}
			}
		};
	}
	
	/**
	 * This function will read the document DOM of the given document and 
	 * set the authorisation status
	 * 
	 * @param detailData
	 * @param data
	 */
	private void checkAuthorisationStatus(final Document detailData, final AddressData data) {
		Elements authStatus = detailData.select(".AuthorisationStatus > h4");	//get the element by the given cssQuery
		
		/*
		 * Check if at least one element was found
		 */
		if (authStatus.size() > 0) {
			String statusText = getText(authStatus.get(0));	//Get the status as a string
			
			/*
			 * Only set the status if it is "Unauthorised" 
			 */
			if (statusText.contains("Unauthorised")) {
				data.setStatus(statusText);
			}
		}
	}
	
	/**
	 * Inside this function, we will extract missing list data
	 * 
	 * @param detailData
	 * @param data
	 */
	private void extractMissingListData(final Document detailData, final AddressData data) {
		if (data.getName() == null || data.getName().trim().isEmpty()) {
			data.setName(getText(detailData.select(".RecordName")));
		}
		if (data.getReferenceNumber() == null || data.getReferenceNumber().trim().isEmpty()) {
			data.setReferenceNumber(getText(detailData.select(".ReferenceNumber > span")));
		}
	}
	
	/**
	 * Here we collect and set the basic detail data from the dom to the AddressData
	 * object
	 * 
	 * @param detailData - the dom to check
	 * @param data - the addressdata to write to
	 */
	private void extractBasicDetailsDataTo(final Document detailData, final AddressData data) {
		Elements basicDetails = detailData.select(".FirmBasicDetailsSection");	//get the elements with the class "FirmBasicDetailsSection"
		
		/*
		 * Iterate over each found element
		 */
		for (Element basic: basicDetails) {
			String label = textFromChildAt(basic, 0);	//get the label
			String value = getTextFromFirstTagInValuePart(basic);	//get the first text
			
			/*
			 * Check if the label contains diffrent text.
			 * Depending on the txt, set diffrent parameters
			 */
			if (contains(label, "Type")) {
				data.setType(value);
			} else if (contains(label, "Current status")) {
				data.setCurrentStatus(value);
			} else if (contains(label, "Effective Date ")) {
				data.setEffectiveDate(value);
			} else if (contains(label, "Sub Status")) {
				data.setSubStatus(value);
			} else if (contains(label, "Insurance Mediation")) {
				data.setInsuranceMediation(value);
			} else if (contains(label, "Agent Status")) {
				data.setAgentStatus(value);
			}
		}
	}
	
	private String getTextFromFirstTagInValuePart(final Element element) {
		String result = null;
		if (element.children().size() == 1) {
			result = element.ownText().trim();
		} else {
			Element tmp = element.children().get(1);
			while (tmp.children().size() > 0) {
				tmp = tmp.children().get(0);
			}
			
			result = tmp.text().trim();
		}
		if (result == null) {
			result = "";
		}
		return result;
	}
	
	/**
	 * Inside this function, we will read the given dom and extract the wanted
	 * principal data to a given PrincipalData object
	 * 
	 * @param detailData
	 * @param data
	 */
	private void extractPrincipalsData(final Document detailData, final PrincipalData data)
	{
		Elements tableElements = detailData.select(".table-striped");	//Get all elements with the class "table-striped"
		
		/*
		 * Iterate over all found elements
		 * 
		 */
		for(Element e : tableElements)
		{
			/*
			 * We are only interested in the element which id contains "princiFirmTable"
			 */
			if(e.children().size() > 1 && e.id().contains("princiFirmTable"))
			{
				Elements tableTds = e.getElementsByTag("td");		//All table cell elements inside the table
				String currentRowService = "";						//The current service name
				StringBuilder otherContent = new StringBuilder();	//A stringbuilder for the content for the 'other' parameter
				
				/*
				 * Iterate over each found table cell
				 */
				for(int i = 0; i < tableTds.size(); i++)
				{
					Element currentTd = tableTds.get(i);	//The current table cell
					
					/*
					 * Check if we at the first table cell of the current row
					 */
					if(i % 4 == 0)
					{
						currentRowService = currentTd.text();	//Set the current service name to the conteent of the table cell
						
						/*
						 * Switch case clause for the currentRowService variable
						 */
						switch(currentRowService)
						{
							case "Euronet Payment Services Ltd":
							{
								data.setRIA(true);				//The value is equals to "Euronet Payment Services Ltd" -> isRIA = true
								break;
							}
							case "MoneyGram International Limited":
							{
								data.setMG(true);				//The value is equals to "MoneyGram International Limited" -> isMG = true
								break;
							}
							case "UK NFS Limited":
							{
								data.setVT(true);				//The value is equals to "UK NFS Limited" -> isVT = true
								break;
							}
							default:
							{
								/*
								 * The first cell has a diffrent value -> append the text to the otherContent stringbuilder
								 */
								otherContent.append(currentRowService);
								break;
							}
						}	
					}
					else
					{
						/*
						 * We are either in the 2th, 3rd or 4rd table cell of the current row
						 */
						if(i % 4 == 1)
							data.setReferenceNumber(currentTd.text());	//Set the reference number
						
						switch(currentRowService)
						{
							case "Euronet Payment Services Ltd":
							{
								if(i % 4 == 2)
									data.setRIAStartDate(currentTd.text());		//3th cell -> set RIAStartDate
								else if(i % 4 == 3)
									data.setRIAEndDate(currentTd.text());		//4th cell -> setRIAEndDate
								
								break;
							}
							case "MoneyGram International Limited":
							{
								if(i % 4 == 2)
									data.setMGStartDate(currentTd.text());		//3th cell -> set MGStartDate
								else if(i % 4 == 3)
									data.setMGEndDate(currentTd.text());		//4th cell -> set MGEndDate
								
								break;
							}
							case "UK NFS Limited":
							{
								if(i % 4 == 2)
									data.setVTStartDate(currentTd.text());		//3th cell -> set VTStartDate
								else if(i % 4 == 3)
									data.setVTEndDate(currentTd.text());		//4th cell -> set VTEndDate
								
								break;
							}
							default:
							{
								/*
								 * In this case, we have e diffrent service, so we fill the 'other' row
								 */
								otherContent.append(" - " + currentTd.text());
							}
						}
					}
				}
				
				data.setOther(otherContent.toString());		//Set other
			}
		}
	}
	
	/**
	 * Within this function we read the given dom and extract the found
	 * address data.
	 * 
	 * @param detailData - the DOM to read
	 * @param data - the AddressData object to write to
	 */
	private void extractAddressDataTo(final Document detailData, final AddressData data) {
		Elements address = detailData.select(".addresssection");		//Get all elements inside the DOM with the class "addresssection"
		
		/*
		 * Iterate over all elements
		 */
		for (Element addressElement: address) {
			
			/*
			 * We are only interested in elements with at least one child.
			 * Also the first child element must have the class
			 * "adresslabel"
			 */
			if (addressElement.children().size() > 1 && addressElement.children().get(0).hasClass("addresslabel")) {
				String label = addressElement.children().get(0).text();			//Get the of the first child 
				String value = addressElement.children().get(1).text().trim();	//Get the trimmed text of the second 
				
				/*
				 * Depending on the "label" text, set diffrent
				 * parameters of the addressdata object
				 */
				if (contains(label, "Phone")) {
					data.setPhone(value);
				} else if (contains(label, "Fax")) {
					data.setFax(value);
				} else if (contains(label, "Email")) {
					data.setEmail(value);
				} else if (contains(label, "Website")) {
					data.setWebsite(value);
				} else if (contains(label, "Address")) {
					Elements addressParts = addressElement.children().get(1).children();
					data.setAddress0(textFromChildAt(addressParts, 0));
					data.setAddress1(textFromChildAt(addressParts, 1));
					data.setAddress2(textFromChildAt(addressParts, 2));
					data.setAddress3(textFromChildAt(addressParts, 3));
					data.setAddress4(textFromChildAt(addressParts, 4));
					data.setAddress5(textFromChildAt(addressParts, 5));
					data.setAddress6(textFromChildAt(addressParts, 6));
					data.setAddress7(textFromChildAt(addressParts, 7));
				}
			}
		}
	}
}
