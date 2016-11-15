package de.ilume.western.union.extractor.data;

/**
 * 
 * @author Dominic Recktenwald
 *
 */
public class PrincipalData {

	/*
	 * All parameters
	 */
	private String referenceNumber;
	private boolean isRIA;
	private String RIAStartDate;
	private String RIAEndDate;
	private boolean isMG;
	private String MGStartDate;
	private String MGEndDate;
	private boolean isVT;
	private String VTStartDate;
	private String VTEndDate;
	private String other;
	
	/*
	 * Getter and setter functions
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public boolean isRIA() {
		return isRIA;
	}
	public void setRIA(boolean isRIA) {
		this.isRIA = isRIA;
	}
	public String getRIAStartDate() {
		if(RIAStartDate == null || RIAStartDate.length() <= 4)
			return "";
		
		return RIAStartDate;
	}
	public void setRIAStartDate(String rIAStartDate) {
		RIAStartDate = rIAStartDate;
	}
	public String getRIAEndDate() {
		if(RIAEndDate == null || RIAEndDate.length() <= 4)
			return "";
		
		return RIAEndDate;
	}
	public void setRIAEndDate(String rIAEndDate) {
		RIAEndDate = rIAEndDate;
	}
	public boolean isMG() {
		return isMG;
	}
	public void setMG(boolean isMG) {
		this.isMG = isMG;
	}
	public String getMGStartDate() {
		if(MGStartDate == null || MGStartDate.length() <= 4)
			return "";
		
		return MGStartDate;
	}
	public void setMGStartDate(String mGStartDate) {
		MGStartDate = mGStartDate;
	}
	public String getMGEndDate() {
		if(MGEndDate == null || MGEndDate.length() <= 4)
			return "";
		
		return MGEndDate;
	}
	public void setMGEndDate(String mGEndDate) {
		MGEndDate = mGEndDate;
	}
	public boolean isVT() {
		return isVT;
	}
	public void setVT(boolean isVT) {
		this.isVT = isVT;
	}
	public String getVTStartDate() {
		if(VTStartDate == null || VTStartDate.length() <= 4)
			return "";
		
		return VTStartDate;
	}
	public void setVTStartDate(String vTStartDate) {
		VTStartDate = vTStartDate;
	}
	public String getVTEndDate() {
		if(VTEndDate == null || VTEndDate.length() <= 4)
			return "";
		
		return VTEndDate;
	}
	public void setVTEndDate(String vTEndDate) {
		VTEndDate = vTEndDate;
	}
	public String getOther() {
		if(other == null || other.length() <= 1)
			return "";
		
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	
	
}
