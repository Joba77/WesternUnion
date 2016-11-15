package de.ilume.western.union.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

public class FcaExtractor {
	
	private static final int CHUNK_SIZE = 2000;
	
	private List<String> linkList;
	
	public FcaExtractor()
	{
		linkList = new ArrayList<String>();
	}

//	@SuppressWarnings("unchecked")
//	public static void main(String[] args) throws IOException {
//		if (args == null || args.length == 0) {
//			System.err.println("Please provide an URL as an argument.");
//			System.exit(1);
//		}
//		
//
//		String id = args[0].substring(args[0].indexOf("id=") + 3);
//		System.out.println("Going to create url list for id: " + id);
//		
//		
//		int pos = 0;
//		int max = 0;
//				
//		Gson gson = new Gson();
//		do {
//			HttpURLConnection con = sendRequest(args, id, pos);
//			
//			int responseCode = con.getResponseCode();
//			if (responseCode != 200) {
//				System.err.println("Error while retrieving data, http error code: " + responseCode);
//			}
//			
//			List<Object> list = new ArrayList<Object>();
//			
//			String response = read(con.getInputStream());
//			List<Object> fromJson = gson.fromJson(response, list.getClass());
//			Map<String, Object> root = (Map<String, Object>) fromJson.get(0);
//			
//			int status = ((Double) root.get("statusCode")).intValue();
//			if (status != 200) {
//				System.err.println("Error while retrieving data, api error code: " + status);
//				System.out.println(response);
//				System.exit(2);
//			}
//			Map<String, Object> resSection = (Map<String, Object>) root.get("result");
//			max = ((Double)resSection.get("iTotalDisplayRecords")).intValue();
//			
//			handleList(resSection);
//			
//			pos += CHUNK_SIZE;
//			
//		} while (pos < max);
//		
//		System.out.println(linkList.size());
//		
//		//At this point, we have all our links in the linkList variable
//	}
	
	public List<String> getLinkList(String url) throws IOException {
		if (url == null || url.equals("")) {
			System.err.println("Please provide an URL as an argument.");
			System.exit(1);
		}
		
		try
		{
			
		String id = url.substring(url.indexOf("id=") + 3);
		//System.out.println("Going to create string list for id: " + id);
		
		int pos = 0;
		int max = 0;
				
		Gson gson = new Gson();
		do {
			HttpURLConnection con = sendRequest(url, id, pos);
			
			int responseCode = con.getResponseCode();
			if (responseCode != 200) {
				System.err.println("Error while retrieving data, http error code: " + responseCode);
			}
			
			List<Object> list = new ArrayList<Object>();
			
			String response = read(con.getInputStream());
			List<Object> fromJson = gson.fromJson(response, list.getClass());
			Map<String, Object> root = (Map<String, Object>) fromJson.get(0);
			
			int status = ((Double) root.get("statusCode")).intValue();
			if (status != 200) {
				System.err.println("Error while retrieving data, api error code: " + status);
				System.out.println(response);
				System.exit(2);
			}
			Map<String, Object> resSection = (Map<String, Object>) root.get("result");
			max = ((Double)resSection.get("iTotalDisplayRecords")).intValue();
			
			handleList(resSection);
			
			pos += CHUNK_SIZE;
			
			System.out.print(".");
		} while (pos < max);
		
		}
		catch(Exception e)
		{
			
		}
		
		
		return linkList;
		
		//At this point, we have all our links in the linkList variable
	}

	private static HttpURLConnection sendRequest(String arg, String id, int pos)
			throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL(createUrl());
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36");
		con.addRequestProperty("Content-Type", "application/json");
		con.addRequestProperty("Referer", arg);
		
		con.setDoOutput(true);
		String createPost = createPost(id, pos);
		createPost = createPost.replaceAll("\\t", "");
		createPost = createPost.replaceAll(" ", "");
		String json = createPost;

		con.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
		con.getOutputStream().flush();
		con.getOutputStream().close();
		return con;
	}

	@SuppressWarnings("unchecked")
	private void handleList(Map<String, Object> map) throws IOException {
		
		List<Object> res = (List<Object>) map.get("aaData");
		for (Object r : res) {
			Map<String, Object> m = (Map<String, Object>) r;
			String resultId = (String) m.get("ShGl_Organisation2__c");
			Map<String, Object> nameMap = (Map<String, Object>) m.get( "ShGl_Organisation2__r" );
			String refNumber = (String) nameMap.get("ShGl_FRN__c");
			String url = "https://register.fca.org.uk/ShPo_firmdetailsPage?id="+resultId;
			linkList.add(url);
		}
	}
	
	private static String createPost(String id, int pos) {
		String json = "{\"action\":\"ShPo_FirmDetailsController\",\"method\":\"appreps\",\"data\":[[{\"name\":\"sEcho\",\"value\":2},{\"name\":\"iColumns\",\"value\":6},{\"name\":\"sColumns\",\"value\":\",,,,,\"},{\"name\":\"iDisplayStart\",\"value\":" + String.valueOf(pos) +"},{\"name\":\"iDisplayLength\",\"value\":" + String.valueOf(CHUNK_SIZE) +"},{\"name\":\"mDataProp_0\",\"value\":\"ShGl_Organisation2__r.Name\"},{\"name\":\"sSearch_0\",\"value\":\"\"},{\"name\":\"bRegex_0\",\"value\":false},{\"name\":\"bSearchable_0\",\"value\":true},{\"name\":\"bSortable_0\",\"value\":false},{\"name\":\"mDataProp_1\",\"value\":\"ShGl_InsuranceMediation__c\"},{\"name\":\"sSearch_1\",\"value\":\"\"},{\"name\":\"bRegex_1\",\"value\":false},{\"name\":\"bSearchable_1\",\"value\":true},{\"name\":\"bSortable_1\",\"value\":false},{\"name\":\"mDataProp_2\",\"value\":\"ShGl_TiedAgent__c\"},{\"name\":\"sSearch_2\",\"value\":\"\"},{\"name\":\"bRegex_2\",\"value\":false},{\"name\":\"bSearchable_2\",\"value\":true},{\"name\":\"bSortable_2\",\"value\":false},{\"name\":\"mDataProp_3\",\"value\":\"ShGl_Organisation2__r.ShGl_FRN__c\"},{\"name\":\"sSearch_3\",\"value\":\"\"},{\"name\":\"bRegex_3\",\"value\":false},{\"name\":\"bSearchable_3\",\"value\":true},{\"name\":\"bSortable_3\",\"value\":false},{\"name\":\"mDataProp_4\",\"value\":\"ShGl_EffectiveDate__c\"},{\"name\":\"sSearch_4\",\"value\":\"\"},{\"name\":\"bRegex_4\",\"value\":false},{\"name\":\"bSearchable_4\",\"value\":true},{\"name\":\"bSortable_4\",\"value\":false},{\"name\":\"mDataProp_5\",\"value\":\"ShGl_Organisation2__r.ShGl_PSDAgentStatus__c\"},{\"name\":\"sSearch_5\",\"value\":\"\"},{\"name\":\"bRegex_5\",\"value\":false},{\"name\":\"bSearchable_5\",\"value\":true},{\"name\":\"bSortable_5\",\"value\":false},{\"name\":\"sSearch\",\"value\":\"\"},{\"name\":\"bRegex\",\"value\":false},{\"name\":\"iSortCol_0\",\"value\":0},{\"name\":\"sSortDir_0\",\"value\":\"asc\"},{\"name\":\"iSortingCols\",\"value\":1},{\"name\":\"orgId\",\"value\":\"" + id + "\"}]],\"type\":\"rpc\",\"tid\":4,\"ctx\":{\"csrf\":\"VmpFPSxNakF4TmkweE1TMHhNRlF4T1RveU56bzFOeTQwTURCYSxZbTZQanc4b2Z6enZqTDRLQmFzVTNZLFlUTXpZalE1\",\"vid\":\"066b0000001Vq92\",\"ns\":\"\",\"ver\":34}}";
		return json;
	}
	
	private static String createUrl() {
		return "https://register.fca.org.uk/apexremote";
	}
	
	 public static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
}
