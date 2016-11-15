package de.ilume.western.union.extractor.printer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import de.ilume.western.union.constants.Constants;
import de.ilume.western.union.extractor.config.Configuration;
import de.ilume.western.union.extractor.data.AddressData;
import de.ilume.western.union.extractor.data.PrincipalData;
import de.ilume.western.union.extractor.util.LoggerUtil;

/**
 * 
 * @author Benedikt Sixt, Dominic Recktenwald
 *
 */
public class CsvPrinter implements IPrinter {
	private static final Logger log = LoggerUtil.getLogger(CsvPrinter.class);
	
	private final PrintWriter addressCSVPrintWriter;
	private final PrintWriter principalCSVPrintWriter;
	
	/**
	 * Constuctor
	 * @param config
	 * @throws IOException
	 */
	public CsvPrinter(final Configuration config) throws IOException {
		Path sinkFile = Paths.get(config.getSinkFile()).getParent();	//Get the path by the given configurations sink file
		
		if (sinkFile != null) {
			Files.createDirectories(sinkFile);		//Create directory			
		}
		
		/*
		 * Create two file objects. One for the address.csv file and
		 * one for the principal.csv file
		 */
		File addressCSVFile = new File(Constants.csvDestinationPath + "/CBI-AddressDetails.csv");
		File prinicipalCSVFile = new File(Constants.csvDestinationPath + "/CBI-Principal.csv");
		
		/*
		 * Initiate both print writer by using the private 
		 * initCSVFile function
		 */
		addressCSVPrintWriter = initCSVFile(addressCSVFile, "Agent Name,B,C,D,Effective Date,Address,City,H,I,J,K,L,M,N,O,P,Q,R,Post Code,Phone,Fax,Email,W,X,Country\n", config);
		principalCSVPrintWriter = initCSVFile(prinicipalCSVFile, "Referencenumber,isRIA,RIAstart,RIAend,isMG,MGstart,MGend,isVT,VTstart,VTend,Other\n", config);		
	}
	
	/**
	 * This function will create a PrintWriter object from the given file, header
	 * and configuration
	 * 
	 * @param f - the file to which the printwriter should write
	 * @param header - the header of the file
	 * @param config - configuration, to check if we have to add a header
	 * @return
	 * @throws FileNotFoundException
	 */
	private PrintWriter initCSVFile(File f, String header, Configuration config) throws FileNotFoundException
	{
		PrintWriter printwriter; 	//The PrintWriter to return
		
		/*
		 * Check if the file exists
		 */
		if(f.exists())
		{
			printwriter = new PrintWriter(new FileOutputStream(f,true)); 	//Set the printwriter in append mode
		}
		else
		{
			printwriter = new PrintWriter(new FileOutputStream(f, true));	//Set the printwriter in append mode TODO, this is the same as abov
			
			if (config.getSinkHeader()) {

				printwriter.append(header);
			}
		}
		
		return printwriter;
	}

	/**
	 * Inside this function, we will fill the both csv files, AddressData and
	 * Principals
	 */
	@Override
	public void print(final AddressData data, final PrincipalData pData) {
		StringBuilder addressBuilder = new StringBuilder();		
		
		//Build the Address file
		addressBuilder.append(" \"" + data.getReferenceNumber() + " - " + data.getName() + "\"");	//Agent Name
		addressBuilder.append(",");//.append("\"" + data.getTradingBrandName() + "\"");	//B
		addressBuilder.append(",").append("\"" + data.getBusinessType() + "\"");		//C
		addressBuilder.append(",").append("\"" + data.getAgentStatus() + "\"");		//D
		addressBuilder.append(",").append("\"" + data.getEffectiveDate() + "\"");		//Effective Date
		addressBuilder.append(",").append("\"" + data.getAddress0() + "\"");			//Address
		addressBuilder.append(",").append("\"" + data.getAddress4() + "\"");			//City
		addressBuilder.append(",").append("\"" + data.getAddress5() + "\"");			//H	
		addressBuilder.append(",").append("\"" + data.getAddress7() + "\"");			//I
		addressBuilder.append(",").append("\"" + data.getAddress3() + "\"");			//J
		addressBuilder.append(",").append("\"" + data.getAddress1() + "\"");			//K
		addressBuilder.append(",").append("\"" + data.getAddress6() + "\"");			//L
		addressBuilder.append(",").append("\"" + data.getAddress2() + "\"");			//M
		addressBuilder.append(",").append("\"" + data.getSubStatus() + "\"");			//N
		addressBuilder.append(",").append("\"" + data.getType() + "\"");				//O
		addressBuilder.append(",").append("\"" + data.getInsuranceMediation() + "\"");	//P
		addressBuilder.append(",").append("\"" + data.getWebsite() + "\"");			//Q
		addressBuilder.append(",").append("\"" + data.getStatus() + "\"");				//R
		addressBuilder.append(",").append("\"" + data.getCurrentStatus() + "\"");		//Post Code
		addressBuilder.append(",").append("\"" + data.getPhone() + "\"");				//Phone
		addressBuilder.append(",").append("\"" + data.getFax() + "\"");				//Fax
		addressBuilder.append(",").append("\"" + data.getEmail() + "\"");				//Email
		addressBuilder.append(",");													//W
		addressBuilder.append(",");													//X
		addressBuilder.append(",").append("\"" + data.getAddress7() + "\"");			//Country
		addressCSVPrintWriter.println(addressBuilder.toString());
		
		StringBuilder principalsBuilder = new StringBuilder();
		
		principalsBuilder.append(" \"" + data.getReferenceNumber() + "\"");
		principalsBuilder.append(",").append("\"" + Boolean.toString(pData.isRIA()) + "\"");
		principalsBuilder.append(",").append("\"" + pData.getRIAStartDate() + "\"");	
		principalsBuilder.append(",").append("\"" + pData.getRIAEndDate() + "\"");	
		principalsBuilder.append(",").append("\"" + Boolean.toString(pData.isMG()) + "\"");
		principalsBuilder.append(",").append("\"" + pData.getMGStartDate() + "\"");
		principalsBuilder.append(",").append("\"" + pData.getMGEndDate() + "\"");
		principalsBuilder.append(",").append("\"" + Boolean.toString(pData.isVT()) + "\"");
		principalsBuilder.append(",").append("\"" + pData.getVTStartDate() + "\"");
		principalsBuilder.append(",").append("\"" + pData.getVTEndDate() + "\"");
		principalsBuilder.append(",").append("\"" + pData.getOther() + "\"");
		principalCSVPrintWriter.println(principalsBuilder.toString());
	}
	
	@Override
	public void close() throws IOException {
		addressCSVPrintWriter.flush();
		addressCSVPrintWriter.close();
		
		principalCSVPrintWriter.flush();
		principalCSVPrintWriter.close();
	}
}
