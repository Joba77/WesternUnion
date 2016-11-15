package de.ilume.western.union.extractor.data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Benedikt Sixt
 *
 */
public class AddressData {
	// list informations
	private String name;
	private String referenceNumber;
	private String link;
	private String tradingBrandName = "";
	private String businessType = "";
	private String status = "";
	// Address
	private String address0 = "";
	private String address1 = "";
	private String address2 = "";
	private String address3 = "";
	private String address4 = "";
	private String address5 = "";
	private String address6 = "";
	private String address7 = "";
	private String phone = "";
	private String fax = "";
	private String email = "";
	private String website = "";
	// basic detail
	private String type = "";
	private String currentStatus = "";
	private String effectiveDate = "";
	private String subStatus = "";
	private String agentStatus = "";
	private String insuranceMediation = "";
	

	private final static List<Method> getters = new ArrayList<>();
	static {
		Method[] methods = AddressData.class.getMethods();
		for (Method method: methods) {
			if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
				getters.add(method);
			}
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AddressData [");
		List<Method> tmpGetters = new ArrayList<>(getters);
		propertyTo(tmpGetters.remove(0), sb);
		for (Method getter: tmpGetters) {
			sb.append("|");
			propertyTo(getter, sb);
		}
		sb.append("]");
		return sb.toString();
	}
	
	private void propertyTo(final Method getter, final StringBuilder sb) {
		sb.append(name(getter)).append(":").append(invoke(getter));
	}
	
	private String name(final Method getter) {
		if (getter == null) {
			return "<null>";
		}
		String name = getter.getName();
		if (name.startsWith("get")) {
			return name.substring(3, 4).toLowerCase() + name.substring(4);
		} else {
			return name.substring(2, 3).toLowerCase() + name.substring(3);
		}
	}
	
	private String invoke(final Method getter) {
		if (getter == null) {
			return "<null>";
		}
		try {
			return String.valueOf(getter.invoke(this));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			return "<" + getter.getName() + ">";
		}
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTradingBrandName() {
		return tradingBrandName;
	}
	public void setTradingBrandName(String tradingBrandName) {
		this.tradingBrandName = tradingBrandName;
	}
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAddress0() {
		return address0;
	}
	public void setAddress0(String address0) {
		this.address0 = address0;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getAddress4() {
		return address4;
	}
	public void setAddress4(String address4) {
		this.address4 = address4;
	}
	public String getAddress5() {
		return address5;
	}
	public void setAddress5(String address5) {
		this.address5 = address5;
	}
	public String getAddress6() {
		return address6;
	}
	public void setAddress6(String address6) {
		this.address6 = address6;
	}
	public String getAddress7() {
		return address7;
	}
	public void setAddress7(String address7) {
		this.address7 = address7;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getSubStatus() {
		return subStatus;
	}
	public void setSubStatus(String subStatus) {
		this.subStatus = subStatus;
	}
	public String getAgentStatus() {
		return agentStatus;
	}
	public void setAgentStatus(String agentStatus) {
		this.agentStatus = agentStatus;
	}
	public String getInsuranceMediation() {
		return insuranceMediation;
	}
	public void setInsuranceMediation(String insuranceMediation) {
		this.insuranceMediation = insuranceMediation;
	}
}
