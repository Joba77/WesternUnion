package de.ilume.western.union.extractor;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import de.ilume.western.union.constants.Constants;
import de.ilume.western.union.extractor.config.Configuration;

public class PRAAddressDetailsExtractorIntegrationTest {
	
//	@Test
//	public void testRunSearchForDPO() {
//		Configuration conf = getTestConfiguration("DPO");
//		PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();
//		extractor.run(conf);
//	}
	
//	@Test
//	public void testRunSearchForPSD() {
//		Configuration conf = getTestConfiguration("PSD");
//		PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();
//		extractor.run(conf);
//	}
//	
	@Test
	public void testRunSearchForReferenceNumber504630() {
		Configuration conf = getTestConfiguration("Euronet Payment Services Ltd");
		PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();
		extractor.run(conf);
	}
//	
	
//	@Test
//	public void testBigRun() {
//		String[] args = {"https://register.fca.org.uk/ShPo_FirmDetailsPage?id=001b000000MfuBnAAJ"};
//		try {
//			ArrayList<String> test = (ArrayList<String>) FcaExtractor.getLinkList(args);
//			System.out.println(test.size());
//			
//			Configuration currentConf;
//			PRAAdressDetailsExtractor extractor = new PRAAdressDetailsExtractor();
//			
//			for(String s : test)
//			{
//				currentConf = getTestConfiguration(s);
//				extractor.run(currentConf);
//			}
//			
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		
//	}
	
	private static Configuration getTestConfiguration(final String searchWord) {
		String[] args = new String[]{"-" + Configuration.PARAMATER_SEARCH_WORD, searchWord};
		
		Configuration conf = new Configuration(args);
		conf.setSinkFile(Constants.csvDestinationPath + "/CBI-AddressDetails.csv");
		conf.setSinkHeader(true);
		conf.setPrintProperties(true);
		conf.setProcessLimit(-1);
		conf.setMsgAfterCount(2);
		return conf;
	}
}
